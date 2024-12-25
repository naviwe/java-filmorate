package ru.yandex.practicum.filmorate.storage.user.friends;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

@Component
@RequiredArgsConstructor
public class UserDbFriends implements UserFriends {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(long id, long friendId) {
        validateUserExists(id);
        validateUserExists(friendId);

        String sql = "INSERT INTO USER_FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?);";
        jdbcTemplate.update(sql, id, friendId);
    }

    @Override
    public void deleteFriend(long id, long friendId) {
        validateUserExists(id);
        validateUserExists(friendId);

        String sql = "DELETE FROM USER_FRIENDS WHERE USER_ID = ? AND FRIEND_ID = ?;";
        jdbcTemplate.update(sql, id, friendId);
    }

    private void validateUserExists(long userId) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);

        if (count == null || count == 0) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден.");
        }
    }
}
