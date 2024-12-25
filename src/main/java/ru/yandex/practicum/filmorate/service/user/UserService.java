package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.friends.UserFriends;

import java.util.Collection;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserFriends userFriendsStorage;

    public Collection<User> findAll() {
        return userStorage.getUsersList();
    }

    public long getSize() {
        return userStorage.getSize();
    }

    public void createUser(User user) {
        userStorage.create(user);
    }

    public void updateUser(User user) {
        userStorage.update(user);
    }

    public void deleteUser(User user) {
        userStorage.deleteById(user.getId());
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public List<User> getUsersInList() {
        return userStorage.getUsersList();
    }

    public void addFriend(long id, long friendId) {
        userFriendsStorage.addFriend(id, friendId);
    }

    public void deleteFriend(long id, long friendId) {
        userFriendsStorage.deleteFriend(id, friendId);
    }

    public List<User> getUserFriends(long id) {
        return userStorage.getUserFriends(id);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

}