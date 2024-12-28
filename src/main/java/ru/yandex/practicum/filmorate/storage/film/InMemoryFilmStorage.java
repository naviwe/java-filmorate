package ru.yandex.practicum.filmorate.storage.film;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new ConcurrentHashMap<>();
    private final Map<Long, Set<Long>> filmLikes = new ConcurrentHashMap<>(); // Хранение лайков: ключ - ID фильма, значение - множество ID пользователей
    private static long nextId = 1L;

    @Override
    public long getSize() {
        return films.size();
    }

    @Override
    public Film getById(Long id) throws FilmNotFoundException {
        Film film = films.get(id);
        if (film == null) throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(nextId);
        nextId++;
        films.put(film.getId(), film);
        filmLikes.put(film.getId(), new HashSet<>()); // Инициализация пустого множества лайков для нового фильма
        return film;
    }

    @SneakyThrows
    @Override
    public void update(Film film) {
        if (film.getId() <= 0 || !films.containsKey(film.getId())) throw new FilmNotFoundException("Фильм не найден");
        films.put(film.getId(), film);
    }

    @Override
    public void deleteById(Long id) {
        films.remove(id);
        filmLikes.remove(id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        validateFilmExists(filmId);
        filmLikes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        validateFilmExists(filmId);
        Set<Long> likes = filmLikes.get(filmId);
        if (likes != null) {
            likes.remove(userId);
        }
    }

    public int getNumberOfLikes(Long filmId) {
        validateFilmExists(filmId);
        return filmLikes.getOrDefault(filmId, Collections.emptySet()).size();
    }

    private void validateFilmExists(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException("Фильм с ID " + filmId + " не найден.");
        }
    }
}
