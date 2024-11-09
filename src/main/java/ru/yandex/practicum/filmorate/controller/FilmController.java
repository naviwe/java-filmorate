package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            film.setId(getNextId());
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм: {}", film);
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка при добавлении фильма: {}", e.getMessage());
            throw e;
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        try {
            if (film.getId() == 0 || !films.containsKey(film.getId())) {
                log.error("Фильм с id={} не найден", film.getId());
                throw new IllegalArgumentException("Фильм с указанным id не найден или id отсутствует.");
            }
            validateFilm(film);
            films.put(film.getId(), film);
            log.info("Фильм с id={} обновлен: {}", film.getId(), film);
            return film;
        } catch (ValidationException e) {
            log.error("Ошибка при обновлении фильма: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может превышать 200 символов.");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }
}
