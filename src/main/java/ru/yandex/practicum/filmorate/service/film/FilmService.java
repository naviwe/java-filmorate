package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserService userService;

    private static Long currentMaxId = 1L;

    @Autowired
    public FilmService(FilmStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    public void deleteAll() {
        currentMaxId = 0L;
        storage.deleteAll();
    }

    public Collection<Film> findAll() {
        return storage.findAll();
    }

    public Film findById(Long id) {
        return storage.findById(id);
    }

    public Film addFilm(Film film) {
        if (LocalDate.of(1895, 12, 28).isAfter(film.getReleaseDate())) {
            throw new ValidationException("Дата релиза фильма раньше 28.12.1895");
        }
        if (!storage.containsKey(film.getId())) {
            film.setId(currentMaxId++);
            Film res = storage.addFilm(film);
            log.info("addFilm: {}", film);
            return res;
        } else {
            throw new ValidationException(String.format("Фильм с id {} уже существует", film.getId()));
        }
    }

    public Film updateFilm(Film film) {
        if (LocalDate.of(1895, 12, 28).isAfter(film.getReleaseDate())) {
            throw new ValidationException("Дата релиза фильма раньше 28.12.1895");
        }
        log.info("updateFilm: {}", film);
        return storage.updateFilm(film);
    }

    public void addLike(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        Film film = storage.findById(filmId);
        User user = userService.findById(userId);

        film.addLike(userId);
        log.info("User: {} was like film: {}", user, film);
    }

    public void remoteLike(Long filmId, Long userId) {
        Film film = storage.findById(filmId);
        User user = userService.findById(userId);
        film.remoteLike(userId);
        log.info("User: {} was remote like film: {}", user, film);
    }

    public List<Film> getMostPopular(Integer count) {
        return storage.getMostPopular(count);
    }
}
