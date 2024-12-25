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
    public User getById(Long id) throws UserNotFoundException {
        String sql = "SELECT u.USER_ID, u.USER_NAME , u.EMAIL, u.LOGIN, u.BIRTHDAY FROM USERS u WHERE u.USER_ID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs, sql), id);
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT u.USER_ID, u.USER_NAME , u.EMAIL, u.LOGIN, u.BIRTHDAY, STRING_AGG(uf.FRIEND_ID, ',')" +
                " AS friends FROM USERS u LEFT JOIN USER_FRIENDS AS uf ON u.user_id = uf.user_id GROUP BY u.user_id;";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs, sql)));
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO USERS (USER_NAME, EMAIL, LOGIN, BIRTHDAY) VALUES (?, ?, ?, ?);";
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
        String sql = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ?";
        long count = jdbcTemplate.queryForObject(sql, Long.class, user.getId());

        if (count == 0) {
            throw new UserNotFoundException("User with id " + user.getId() + " not found");
        }

        jdbcTemplate.update("UPDATE USERS SET USER_NAME = ?, EMAIL = ?, LOGIN = ?, BIRTHDAY = ? WHERE USER_ID = ?;",
                user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
    }


    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM USER_FRIENDS WHERE USER_ID = ?;", id);
        jdbcTemplate.update("DELETE FROM USERS WHERE USER_ID = ?;", id);
    }

    @Override
    public void addFriend(Long userId, Long friendToAddId) throws UserNotFoundException {
        getById(userId);
        getById(friendToAddId);

        String sql = "INSERT INTO USER_FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendToAddId);
        jdbcTemplate.update(sql, friendToAddId, userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendToDeleteId) throws UserNotFoundException {
        getById(userId);
        getById(friendToDeleteId);

        String sql = "DELETE FROM USER_FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, userId, friendToDeleteId);
        jdbcTemplate.update(sql, friendToDeleteId, userId);
    }

    @Override
    public Set<Long> getUserFriends(Long userId) throws UserNotFoundException {
        getById(userId);

        String sql = "SELECT FRIEND_ID FROM USER_FRIENDS WHERE USER_ID = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, userId));
    }


    @Override
    public Collection<Long> getMutualFriends(Long userId1, Long userId2) throws UserNotFoundException {
        String sql = "SELECT uf.FRIEND_ID " +
                "FROM USER_FRIENDS uf " +
                "WHERE uf.USER_ID = ? " +
                "AND uf.FRIEND_ID IN (SELECT FRIEND_ID FROM USER_FRIENDS WHERE USER_ID = ?)";
        return jdbcTemplate.queryForList(sql, Long.class, userId1, userId2);
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
}