package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;


import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public Collection<User> findAll() {
        log.info("findAll() method called in UserController");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) throws UserNotFoundException, FilmNotFoundException {
        log.info("getUser() method called with id = {}", id);
        return userService.getById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getUserFriends(@PathVariable Long id) throws UserNotFoundException, FilmNotFoundException {
        log.info("getUserFriends() method called for user with id = {}", id);
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(@PathVariable Long id, @PathVariable Long otherId) throws UserNotFoundException, FilmNotFoundException {
        log.info("getMutualFriends() method called for users with ids {} and {}", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }


    @PostMapping()
    public User create(@Valid @RequestBody @NonNull User user) throws ValidationException {
        user = userService.create(user);
        log.info("user created with id = {}, number of users = {}", user.getId(), userService.getSize());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody @NonNull User user) {
        userService.update(user);
        log.info("user with id = {} updated or created", user.getId());
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void makeFriends(@PathVariable Long id, @PathVariable Long friendId) throws UserNotFoundException, FilmNotFoundException {
        log.info("makeFriends() method called for users with ids {} and {}", id, friendId);
        userService.makeFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendship(@PathVariable Long id, @PathVariable Long friendId) throws UserNotFoundException, FilmNotFoundException {
        log.info("deleteFriend() method called for users with ids {} and {}", id, friendId);
        userService.deleteFriend(id, friendId);
    }
}