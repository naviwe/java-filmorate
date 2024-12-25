package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getById(Long id) throws UserNotFoundException {
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

    public void makeFriends(Long userId, Long friendToAddId) throws UserNotFoundException {
        userStorage.addFriend(userId, friendToAddId);
        userStorage.addFriend(friendToAddId, userId);
    }


    public void deleteFriend(Long userId, Long friendToDellId) throws UserNotFoundException {
        userStorage.deleteFriend(userId, friendToDellId);
        userStorage.deleteFriend(friendToDellId, userId);
    }


    public Collection<User> getMutualFriends(Long userId1, Long userId2) throws UserNotFoundException {
        Collection<Long> mutualFriendsIds = userStorage.getMutualFriends(userId1, userId2);

        log.info("Found {} mutual friends between user with id {} and user with id {}",
                mutualFriendsIds.size(), userId1, userId2);

        return mutualFriendsIds.stream()
                .map(friendId -> {
                    try {
                        return getById(friendId);
                    } catch (UserNotFoundException e) {
                        log.error("User with id {} not found while retrieving mutual friend", friendId);
                        return null;
                    }
                })
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }


    public Collection<User> getUserFriends(Long userId) throws UserNotFoundException {
        Set<Long> friendsIds = userStorage.getUserFriends(userId);

        log.info("User with id = {} has {} friends", userId, friendsIds.size());

        return friendsIds.stream()
                .map(friendId -> {
                    try {
                        return getById(friendId);
                    } catch (UserNotFoundException e) {
                        log.error("Error occurred while retrieving friend with id = {}: {}", friendId, e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

}