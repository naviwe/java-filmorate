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
import java.util.List;
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
        if (film == null) {
            throw new FilmNotFoundException("Film with id " + id + " not found");
        }
        return film;
    }


    public Film create(Film film) throws ValidationException {
        validateFilm(film);
        film = filmStorage.create(film);
        return film;
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDATE)) {
            log.warn("Film validation fail");
            throw new ValidationException("The film was released before 28.12.1895");
        }
    }

    public long getSize() {
        return filmStorage.getSize();
    }

    public Film update(Film film) throws ValidationException {
        validateFilm(film);
        filmStorage.update(film);
        log.info("Film with id {} updated", film.getId());
        return film;
    }

    public void likeFromUser(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        Film filmExistant = filmStorage.getById(filmId);
        userStorage.getUserById(userId);
        filmExistant.addLike(userId);
    }

    public void unlikeFromUser(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        Film filmExistant = filmStorage.getById(filmId);
        userStorage.getUserById(userId);
        filmExistant.removeLike(userId);
    }

    private int getNumberOfLikes(Long filmId) throws UserNotFoundException, FilmNotFoundException {
        Film filmExistant = filmStorage.getById(filmId);
        return filmExistant.getUsersIdsLiked().size();
    }

    public Collection<Film> getCountTopFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Count must be greater than 0");
        }

        List<Film> allFilms = filmStorage.findAll().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());

        if (allFilms.isEmpty()) {
            log.info("No popular films found.");
        }

        return allFilms;
    }







}