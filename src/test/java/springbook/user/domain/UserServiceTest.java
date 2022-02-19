package springbook.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static springbook.user.domain.UserServiceImpl.MAX_RECOMMEND_FOR_GOLD;
import static springbook.user.domain.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "file:src/main/resources/applicationContext.xml")
class UserServiceTest {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private UserServiceImpl userServiceImpl;
    private List<User> users;

    @BeforeEach
    void setUp() {
        users = Arrays.asList(
            new User("id1","name1","pwd1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "aa@bb.cc"),
            new User("id2","name2","pwd2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "aa@bb.cc"),
            new User("id3","name3","pwd3", Level.SILVER, 60, MAX_RECOMMEND_FOR_GOLD-1, "aa@bb.cc"),
            new User("id4","name4","pwd4", Level.SILVER, 60, MAX_RECOMMEND_FOR_GOLD, "aa@bb.cc"),
            new User("id5","name5","pwd5", Level.GOLD, 100, 100, "aa@bb.cc")
        );
    }

    @Test
    void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userServiceImpl.add(userWithLevel);
        userServiceImpl.add(userWithoutLevel);

        User userWithLevelGet = userDao.get(userWithLevel.getId());
        User userWithoutLevelGet = userDao.get(userWithoutLevel.getId());

        assertEquals(Level.GOLD, userWithLevelGet.getLevel());
        assertEquals(Level.BASIC, userWithoutLevelGet.getLevel());
    }

    @Test
    void upgradeLevels() {
        userDao.deleteAll();
        for(User user : users)
            userDao.add(user);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);
        userServiceImpl.upgradeLevels();

        assertTrue(checkLevel(users.get(0), Level.BASIC));
        assertTrue(checkLevel(users.get(1), Level.SILVER));
        assertTrue(checkLevel(users.get(2), Level.SILVER));
        assertTrue(checkLevel(users.get(3), Level.GOLD));
        assertTrue(checkLevel(users.get(4), Level.GOLD));

        List<String> requests = mockMailSender.getRequests();
        assertEquals(2, requests.size());
        assertEquals(users.get(1).getEmail(), requests.get(0));
        assertEquals(users.get(3).getEmail(), requests.get(1));
    }

    @Test
    void upgradeAllOrNothing() {
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
        testUserService.setMailSender(mailSender);

        UserServiceTx userServiceTx = new UserServiceTx();
        userServiceTx.setUserService(testUserService);
        userServiceTx.setTransactionManager(transactionManager);

        userDao.deleteAll();
        for(User user : users)
            userDao.add(user);

        try {
            userServiceTx.upgradeLevels();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        assertTrue(checkLevel(users.get(1), Level.BASIC));
        assertTrue(checkLevel(users.get(3), Level.SILVER));
    }

    private boolean checkLevel(User user, Level expected) {
        User get = userDao.get(user.getId());
        return get.getLevel().equals(expected);
    }
}