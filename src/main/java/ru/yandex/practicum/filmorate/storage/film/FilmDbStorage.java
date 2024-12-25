package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Primary
@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public long getSize() {
        String sql = "SELECT COUNT(*) FROM FILMS";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }


    @Override
    public Film getById(Long id) throws FilmNotFoundException {
        String sql = "SELECT f.FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, fr.RATING, fr.RATING_ID, " +
                "STRING_AGG(DISTINCT gi.GENRE_ID || '-' || gi.GENRE_NAME, ',') AS genre " +
                "FROM FILMS AS f " +
                "LEFT JOIN FILM_RATING fr ON f.RATING_ID = fr.RATING_ID " +
                "LEFT JOIN FILM_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRE_INFO gi ON fg.GENRE_ID = gi.GENRE_ID " +
                "WHERE f.FILM_ID = ? " +
                "GROUP BY f.FILM_ID, fr.RATING_ID, fr.RATING;";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs, sql), id);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException("Фильм с ID " + id + " не найден");
        }
    }


    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.FILM_ID, FILM_NAME,DESCRIPTION,RELEASE_DATE, DURATION,fr.RATING , fr.RATING_ID, " +
                "STRING_AGG(DISTINCT gi.GENRE_ID || '-' || gi.GENRE_NAME, ',') AS genre " +
                "FROM FILMS  AS f " +
                "LEFT JOIN FILM_RATING fr ON f.RATING_ID = fr.RATING_ID " +
                "LEFT JOIN FILM_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRE_INFO gi ON fg.GENRE_ID = gi.GENRE_ID " +
                "GROUP BY f.FILM_ID;";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, sql)));
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());

        long filmId = jdbcTemplate.queryForObject("SELECT FILM_ID FROM FILMS ORDER BY FILM_ID DESC LIMIT 1", Integer.class);
        film.setId(filmId);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }

        return film;
    }


    @Override
    public void update(Film film) {
        jdbcTemplate.update("UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, " +
                        "RATING_ID = ? WHERE FILM_ID = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());

        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID = ?", film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
    }



    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID = ? ", id);
        jdbcTemplate.update("DELETE FROM FILMS where FILM_ID = ? ", id);
    }

    private Film makeFilm(ResultSet rs, String sql) throws SQLException {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        String genreString = rs.getString("genre");
        if (genreString != null && !genreString.isEmpty()) {
            genres = Arrays.stream(genreString.split(","))
                    .map(genre -> {
                        String[] parts = genre.split("-");
                        return new Genre(Integer.parseInt(parts[0]), parts[1]);
                    })
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        Set<Long> likes = new HashSet<>();
        if (sql.contains("user_likes")) {
            String likesString = rs.getString("user_likes");
            if (likesString != null && !likesString.isEmpty()) {
                likes = Arrays.stream(likesString.split(","))
                        .map(Long::valueOf)
                        .collect(Collectors.toSet());
            }
        }

        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("rating_id"), rs.getString("rating")))
                .genres(genres)
                .likes(likes)
                .build();
    }

}