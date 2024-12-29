package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenresStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenresService {

    private final GenresStorage genresStorage;

    public List<Genre> getAllGenres() {
        return genresStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return genresStorage.getGenreById(id);
    }

}