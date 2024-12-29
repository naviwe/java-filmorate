package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    public static final LocalDate CINEMA_BIRTHDATE = LocalDate.of(1895, 12, 28);

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getById(Long id) {
        Film film = filmStorage.getById(id);
        return film;
    }

    public Film create(Film film) throws ValidationException {
        validateFilm(film);
        film = filmStorage.create(film);
        return film;
    }

    public Film update(Film film) throws ValidationException {
        validateFilm(film);
        filmStorage.update(film);
        log.info("Film with id {} updated", film.getId());
        return film;
    }

    public void likeFromUser(Long filmId, Long userId) {
        validateFilmAndUser(filmId, userId);
        filmStorage.addLike(filmId, userId);
        log.info("User with ID {} liked film with ID {}", userId, filmId);
    }

    public void unlikeFromUser(Long filmId, Long userId) {
        validateFilmAndUser(filmId, userId);
        filmStorage.removeLike(filmId, userId);
        log.info("User with ID {} unliked film with ID {}", userId, filmId);
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDATE)) {
            log.warn("Film validation fail");
            throw new ValidationException("The film was released before 28.12.1895");
        }
    }

    private void validateFilmAndUser(Long filmId, Long userId) {
        if (filmStorage.getById(filmId) == null) {
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден.");
        }
        if (userStorage.getUserById(userId) == null) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден.");
        }
    }

    private int getNumberOfLikes(Long filmId) {
        return filmStorage.getNumberOfLikes(filmId);
    }

    public Collection<Film> getCountTopFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Count must be greater than 0");
        }

        return filmStorage.findAll().stream()
                .sorted((film1, film2) -> Integer.compare(getNumberOfLikes(film2.getId()), getNumberOfLikes(film1.getId())))
                .limit(count)
                .collect(Collectors.toList());
    }

    public long getSize() {
        return filmStorage.getSize();
    }
}

