package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
public class UserService {
    private static Long currentMaxId = 0L;
    private UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }


    public Collection<User> findAll() {
        return storage.findAll();
    }


    public User findById(Long id) {
        return storage.findById(id);
    }


    public User createUser(User user) {
        user.setId(currentMaxId++);
        storage.addUser(user);

        return user;
    }


    public User updateUser(User user) {
        return storage.updateUser(user);
    }


    public void addFriend(Long id, Long friendId) {
        User userFriend = storage.findById(friendId);
        Friend friend = new Friend(friendId);
        if (!userFriend.getFriends().stream().findFirst().isEmpty()) {
            friend.setCross(true);
        } else {
            friend.setCross(false);
        }
        storage.findById(id).addFriend(friend);
    }


    public void removeFriend(Long id, Long userId) {
        storage.findById(id).getFriends().remove(userId);
    }


    public Collection<User> getFriends(Long id) {
        return storage.getUserFriends(id);
    }


    public Collection<User> getCrossFriends(Long id, Long userId) {
        return storage.getUserCrossFriends(id, userId);
    }


    public void deleteAll() {
        storage.deleteAll();
    }
}
