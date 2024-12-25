package ru.yandex.practicum.filmorate.storage.film;

import lombok.SneakyThrows;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;

public interface FilmStorage {


    long getSize();

    Film getById(Long id) throws FilmNotFoundException;

    Collection<Film> findAll();

    Film create(Film film);

    @SneakyThrows
    void update(Film film);

    void deleteById(Long id);
}
