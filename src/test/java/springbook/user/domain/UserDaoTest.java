package springbook.user.domain;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    @Test
    void addAndGet() throws SQLException, ClassNotFoundException {
        ConnectionMaker connectionMaker = new DConnectionMaker();
        UserDao dao = new UserDao(connectionMaker);

        User user = new User();
        user.setId("id");
        user.setName("name");
        user.setPassword("pwd");

        dao.add(user);

        System.out.println(user.getId() + " 등록성공");

        User get = dao.get(user.getId());
        System.out.println(get.getId() + " 조회성공");
    }
}