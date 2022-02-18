package springbook.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "file:src/main/resources/applicationContext.xml")
class UserDaoTest {
    @Autowired
    private UserDao dao;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
          user1 = new User("id1","name1","pwd1");
        user2 = new User("id2","name2","pwd2");
        user3 = new User("id3","name3","pwd3");
    }

    @Test
    void addAndGet() throws SQLException {
        dao.deleteAll();
        assertEquals(                              0, dao.getCount());

        dao.add(user1);
        dao.add(user2);
        assertEquals(2, dao.getCount());

        User get1 = dao.get(user1.getId());
        User get2 = dao.get(user2.getId());
        assertEquals(user1, get1);
        assertEquals(user2, get2);
    }

    @Test
    void getCount() throws SQLException {
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
    void getUserFailure() throws SQLException {
        dao.deleteAll();
        assertEquals(0, dao.getCount());

        assertThrows(EmptyResultDataAccessException.class, ()->dao.get("UnknownId"));
    }
}