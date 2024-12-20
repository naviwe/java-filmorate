package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
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

    public Film getById(Long id) throws UserNotFoundException, FilmNotFoundException {
        return filmStorage.getById(id);
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
        userStorage.getById(userId);
        filmExistant.addLike(userId);
    }

    public void unlikeFromUser(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        Film filmExistant = filmStorage.getById(filmId);
        userStorage.getById(userId);
        filmExistant.removeLike(userId);
    }

    private int getNumberOfLikes(Long filmId) throws UserNotFoundException, FilmNotFoundException {
        Film filmExistant = filmStorage.getById(filmId);
        return filmExistant.getUsersIdsLiked().size();
    }

    public Collection<Film> getCountTopFilms(int count) {
        try {
            return filmStorage.findAll().stream()
                    .sorted((film1, film2) -> film2.getUsersIdsLiked().size() - film1.getUsersIdsLiked().size())
                    .limit(count)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new FilmNotFoundException("Error while fetching top films: " + e.getMessage());
        }
    }


}