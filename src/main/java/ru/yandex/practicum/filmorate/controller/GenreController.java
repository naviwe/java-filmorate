package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.service.film.GenresService;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/genres")
public class GenreController {

    private final GenresService genresService;

    @GetMapping
    public List<Genre> getAllGenres() {
        log.info("Request received: GET /genres");
        List<Genre> genres = genresService.getAllGenres();
        log.info("Response: {} genres found", genres.size());
        return genres;
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable long id) throws NotFoundException {
        log.info("Request received: GET /genres/{}", id);
        Genre genre = genresService.getGenreById((int) id);
        log.info("Response: Genre found - {}", genre);
        return genre;
    }
}
