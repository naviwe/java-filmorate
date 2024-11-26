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

    public Collection<User> getMutualFriends(Long userId1, Long userId2) throws UserNotFoundException {
        Collection<Long> mutualFriendsIds = getMutualFriendsIds(userId1, userId2);

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

    public Collection<Long> getMutualFriendsIds(Long user1Id, Long user2Id) throws UserNotFoundException {
        User user1 = getById(user1Id);
        User user2 = getById(user2Id);

        Set<Long> user1Friends = user1.getFriends();
        Set<Long> user2Friends = user2.getFriends();

        return user1Friends.stream()
                .filter(user2Friends::contains)
                .collect(Collectors.toList());
    }


    public Collection<User> getUserFriends(Long userId) throws UserNotFoundException, FilmNotFoundException {
        log.info("getUserFriends() method called for user with id = {}", userId);

        Collection<Long> friendsIds = userStorage.getById(userId).getFriends();
        log.info("User with id = {} has {} friends", userId, friendsIds.size());

        Collection<User> friends = friendsIds.stream()
                .map(friendId -> {
                    try {
                        User friend = userStorage.getById(friendId);
                        log.info("Retrieved friend with id = {}", friend.getId());
                        return friend;
                    } catch (UserNotFoundException | FilmNotFoundException e) {
                        log.error("Error occurred while retrieving friend with id = {}: {}", friendId, e.getMessage());
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        log.info("Returning {} friends for user with id = {}", friends.size(), userId);
        return friends;
    }
}