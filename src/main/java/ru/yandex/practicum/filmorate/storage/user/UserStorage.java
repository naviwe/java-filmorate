package ru.yandex.practicum.filmorate.storage.user;

import lombok.SneakyThrows;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    long getSize();

    User getById(Long id) throws UserNotFoundException;

    Collection<User> findAll();

    User create(User user);

    @SneakyThrows
    void update(User user);

    void deleteById(Long id);
}
