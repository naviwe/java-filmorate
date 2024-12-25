package ru.yandex.practicum.filmorate.storage.user;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private static long nextId = 1L;

    @Override
    public long getSize() {
        return users.size();
    }

    @Override
    public User getById(Long id) throws UserNotFoundException {
        User user = users.get(id);
        if (user == null) throw new UserNotFoundException("Пользовартель с id: " + id + " не найден");
        return users.get(id);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
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
    public void addFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        if (user != null && friend != null) {
            user.addFriend(friendId);
            friend.addFriend(userId);
        }
    }


    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        if (user != null && friend != null) {
            user.deleteFriend(friendId);
            friend.deleteFriend(userId);
        }
    }


    @Override
    public Set<Long> getUserFriends(Long userId) {
        User user = users.get(userId);
        if (user != null) {
            return user.getFriends();
        }
        return Collections.emptySet();
    }


    @Override
    public Collection<Long> getMutualFriends(Long userId1, Long userId2) {
        User user1 = users.get(userId1);
        User user2 = users.get(userId2);

        if (user1 != null && user2 != null) {
            Set<Long> user1Friends = user1.getFriends();
            Set<Long> user2Friends = user2.getFriends();

            user1Friends.retainAll(user2Friends);
            return user1Friends;
        }
        return Collections.emptySet();
    }

}