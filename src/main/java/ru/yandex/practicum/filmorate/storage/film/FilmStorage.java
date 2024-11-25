package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    void deleteAll();

    boolean containsKey(Long id);

    Collection<Film> findAll();

    Film findById(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getMostPopular(Integer count);
}
