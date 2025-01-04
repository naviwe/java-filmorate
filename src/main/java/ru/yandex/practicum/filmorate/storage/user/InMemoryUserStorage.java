package ru.yandex.practicum.filmorate.storage.user;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private static long nextId = 1L;

    @Override
    public long getSize() {
        return users.size();
    }

    @Override
    public User getUserById(Long id) {
        return getUsersMap().get(id);
    }

    @Override
    public User create(User user) {
        user.setId(nextId);
        nextId++;
        users.put(user.getId(), user);
        return user;
    }

    @SneakyThrows
    @Override
    public void update(User user) {
        if (user.getId() <= 0 || !users.containsKey(user.getId()))
            throw new UserNotFoundException("Пользователь с id: " + user.getId() + " не найден ");
        users.put(user.getId(), user);
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> getUsersList() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Map<Long, User> getUsersMap() {
        return users;
    }

    @Override
    public List<User> getUserFriends(Long userId) {
        return getUsersList().stream().filter(x -> getUsersMap().get(userId).getFriends().contains(x.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long userId1, Long userId2) {
        return getUsersList().stream().filter(x -> getUsersMap().get(userId1).getFriends()
                .contains(x.getId())).filter(x -> getUsersMap().get(userId2).getFriends()
                .contains(x.getId())).collect(Collectors.toList());
    }
}