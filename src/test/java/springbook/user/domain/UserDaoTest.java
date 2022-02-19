package springbook.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "file:src/main/resources/applicationContext.xml")
class UserDaoTest {
    @Autowired
    private UserDaoJdbc dao;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = new User("id1","name1","pwd1", Level.BASIC, 1, 0, "aa@bb.cc");
        user2 = new User("id2","name2","pwd2", Level.SILVER, 55, 10, "aa@bb.cc");
        user3 = new User("id3","name3","pwd3", Level.GOLD, 100, 40, "aa@bb.cc");
    }

    @Test
    void addAndGet() {
        dao.deleteAll();
        assertEquals(0, dao.getCount());

        dao.add(user1);
        dao.add(user2);
        assertEquals(2, dao.getCount());

        User get1 = dao.get(user1.getId());
        User get2 = dao.get(user2.getId());
        assertEquals(user1, get1);
        assertEquals(user2, get2);
    }

    @Test
    void getCount() {
        dao.deleteAll();
        assertEquals(0, dao.getCount());

        dao.add(user1);
        assertEquals(1, dao.getCount());

        dao.add(user2);
        assertEquals(2, dao.getCount());

        dao.add(user3);
        assertEquals(3, dao.getCount());
    }

    @Test
    void getUserFailure() {
        dao.deleteAll();
        assertEquals(0, dao.getCount());

        assertThrows(EmptyResultDataAccessException.class, ()->dao.get("UnknownId"));
    }

    @Test
    void duplicateKey() {
        dao.deleteAll();
        dao.add(user1);
        assertThrows(DataAccessException.class, ()->dao.add(user1));
    }

    @Test
    void getAll() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);
        dao.add(user3);

        List<User> users = dao.getAll();
        assertEquals(3, users.size());

        assertEquals(user1, users.get(0));
        assertEquals(user2, users.get(1));
        assertEquals(user3, users.get(2));
    }

    @Test
    void update() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);
        dao.add(user3);

        user1.setName("mod1");
        dao.update(user1);

        List<User> users = dao.getAll();

        assertEquals(user1, users.get(0));
        assertEquals(user2, users.get(1));
        assertEquals(user3, users.get(2));
    }
}