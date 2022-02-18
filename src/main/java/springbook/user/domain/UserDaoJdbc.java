package springbook.user.domain;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoJdbc implements UserDao {
    private JdbcTemplate jdbcTemplate;
    private RowMapper<User> rowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            return user;
        }
    };

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) {
        jdbcTemplate.update("insert into users(id, name, password) values(?, ?, ?)",
                user.getId(),
                user.getName(),
                user.getPassword());
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from users");
    }

    public User get(String id) {
        return jdbcTemplate.queryForObject("select * from users where id = ?", rowMapper, id);
    }

    public List<User> getAll() {
        return jdbcTemplate.query("select * from users order by id", rowMapper);
    }

    public int getCount() {
        return jdbcTemplate.queryForObject("select count(*) from users", int.class);
    }
}
