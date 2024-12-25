package ru.yandex.practicum.filmorate.storage.user;

import lombok.SneakyThrows;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    long getSize();

    User getById(Long id) throws UserNotFoundException;

    Collection<User> findAll();

    User create(User user);

    @SneakyThrows
    void update(User user);

    void deleteById(Long id);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Set<Long> getUserFriends(Long userId);

    Collection<Long> getMutualFriends(Long userId1, Long userId2);

}
