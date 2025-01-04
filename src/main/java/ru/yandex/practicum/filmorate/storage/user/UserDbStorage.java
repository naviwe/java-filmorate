package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Primary
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public long getSize() {
        String sql = "SELECT COUNT(*) FROM USERS";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }


    @Override
    public User getUserById(Long id) throws UserNotFoundException {
        String sql = "SELECT u.USER_ID, u.USER_NAME , u.EMAIL, u.LOGIN, u.BIRTHDAY FROM USERS u WHERE u.USER_ID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs, sql), id);
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO USERS (USER_NAME, EMAIL, LOGIN, BIRTHDAY) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday());

        long id = jdbcTemplate.queryForObject("SELECT USER_ID FROM USERS ORDER BY USER_ID DESC LIMIT 1", Long.class);
        user.setId(id);

        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
            for (Long friendId : user.getFriends()) {
                jdbcTemplate.update("INSERT INTO USER_FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)", user.getId(), friendId);
            }
        }

        return user;
    }


    @Override
    public void update(User user) {
        String checkSql = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, user.getId());

        if (count == null || count == 0) {
            throw new UserNotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }

        String updateSql = "UPDATE USERS SET USER_NAME = ?, EMAIL = ?, LOGIN = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(updateSql, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
    }


    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM USERS WHERE user_id =?;", id);
        jdbcTemplate.update("DELETE FROM USERS WHERE user_id =?;", id);
    }

    @Override
    public List<User> getUsersList() {
        String sql = "SELECT u.USER_ID, u.USER_NAME , u.EMAIL, u.LOGIN, u.BIRTHDAY, STRING_AGG(uf.FRIEND_ID, ',')" +
                " AS friends FROM USERS u LEFT JOIN USER_FRIENDS AS uf ON u.user_id = uf.user_id GROUP BY u.user_id;";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs, sql)));
    }

    @Override
    public Map<Long, User> getUsersMap() {
        return getUsersList().stream().collect(Collectors.toMap(User::getId, user -> user));
    }

    @Override
    public List<User> getUserFriends(Long userId) {
        validateUserExists(userId);
        String sql = "SELECT u.USER_ID, u.USER_NAME, u.EMAIL, u.LOGIN, u.BIRTHDAY FROM USERS u " +
                "WHERE u.USER_ID IN (SELECT uf.FRIEND_ID FROM USER_FRIENDS uf WHERE uf.user_id = ?);";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs, sql), userId));
    }

    @Override
    public List<User> getCommonFriends(Long userId1, Long userId2) {
        String sql = " SELECT u.USER_ID, u.USER_NAME , u.EMAIL, u.LOGIN, u.BIRTHDAY FROM USERS u " +
                "WHERE u.USER_ID IN (SELECT uf.FRIEND_ID FROM USER_FRIENDS uf " +
                "GROUP BY uf.FRIEND_ID " +
                "HAVING STRING_AGG(uf.USER_ID , '') LIKE ? AND STRING_AGG(uf.USER_ID , '') LIKE ? ); ";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs, sql),
                "%" + userId1 + "%", "%" + userId2 + "%"));
    }


    private User makeUser(ResultSet rs, String sql) throws SQLException {
        Set<Long> friends = new HashSet<>();
        if (sql.contains("friends")) {
            Optional<Array> userFriends = Optional.ofNullable(rs.getArray("friends"));
            if (userFriends.isPresent()) {
                friends = Arrays.stream((Object[]) userFriends.get().getArray()).map(Object::toString)
                        .flatMap(Pattern.compile(",")::splitAsStream).map(Long::valueOf)
                        .collect(Collectors.toSet());
            }
        }
        return User.builder()
                .id(rs.getLong("user_id"))
                .name(rs.getString("user_name"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friends(friends)
                .build();
    }

    private void validateUserExists(Long userId) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);

        if (count == null || count == 0) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден.");
        }
    }

}