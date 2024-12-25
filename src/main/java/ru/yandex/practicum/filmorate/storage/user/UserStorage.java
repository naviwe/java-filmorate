package ru.yandex.practicum.filmorate.storage.user;

import lombok.SneakyThrows;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserStorage {

    long getSize();

    User getUserById(Long id) throws UserNotFoundException;

    User create(User user);

    @SneakyThrows
    void update(User user);

    void deleteById(Long id);

    List<User> getUsersList();

    Map<Long, User> getUsersMap();

    List<User> getUserFriends(Long userId);

    List<User> getCommonFriends(Long userId1, Long userId2);

}
