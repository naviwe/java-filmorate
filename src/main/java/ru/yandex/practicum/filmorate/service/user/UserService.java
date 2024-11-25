package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getById(Long id) throws UserNotFoundException, FilmNotFoundException {
        return userStorage.getById(id);
    }

    public long getSize() {
        return userStorage.getSize();
    }

    public User create(User user) {
        user = userStorage.create(user);
        return user;
    }

    public User update(User user) {
        userStorage.update(user);
        return user;
    }

    public void makeFriends(Long userId, Long friendToAddId) throws UserNotFoundException, FilmNotFoundException {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendToAddId);
        user.addFriend(friendToAddId);
        friend.addFriend(userId);
    }

    public void deleteFriend(Long userId, Long friendToDellId) throws UserNotFoundException, FilmNotFoundException {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendToDellId);
        user.deleteFriend(friendToDellId);
        friend.deleteFriend(userId);
    }

    public Collection<Long> getMutualFriendsIds(Long user1Id, Long user2Id) throws UserNotFoundException {
        User user1 = userStorage.getById(user1Id);
        User user2 = userStorage.getById(user2Id);

        Set<Long> user1Friends = user1.getFriends();
        Set<Long> user2Friends = user2.getFriends();

        return user1Friends.stream()
                .filter(user2Friends::contains)
                .collect(Collectors.toList());
    }

}