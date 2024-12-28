package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Fetching all films...");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        log.info("Fetching film with ID {}", id);
        return filmService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        Film createdFilm = filmService.create(film);
        log.info("Film created: {}", createdFilm);
        return createdFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        Film updatedFilm = filmService.update(film);
        log.info("Film updated: {}", updatedFilm);
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLikeFromUser(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        log.info("Adding like for film ID {} from user ID {}", filmId, userId);
        filmService.likeFromUser(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLikeFromUser(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        log.info("Removing like for film ID {} from user ID {}", filmId, userId);
        filmService.unlikeFromUser(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getCountTop(@RequestParam(defaultValue = "10") @Min(1) int count) {
        log.info("Fetching top {} popular films", count);
        return filmService.getCountTopFilms(count);
    }
}
