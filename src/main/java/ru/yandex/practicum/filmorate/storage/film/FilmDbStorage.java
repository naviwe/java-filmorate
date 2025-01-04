package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.GenreCreationException;
import ru.yandex.practicum.filmorate.exception.MpaCreationException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
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
            log.warn("Film with ID {} not found", id);
            throw new FilmNotFoundException("Фильм с ID " + id + " не найден.");
        }
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.FILM_ID, FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, fr.RATING, fr.RATING_ID, " +
                "STRING_AGG(DISTINCT gi.GENRE_ID || '-' || gi.GENRE_NAME, ',') AS genre " +
                "FROM FILMS AS f " +
                "LEFT JOIN FILM_RATING fr ON f.RATING_ID = fr.RATING_ID " +
                "LEFT JOIN FILM_GENRES fg ON f.FILM_ID = fg.FILM_ID " +
                "LEFT JOIN GENRE_INFO gi ON fg.GENRE_ID = gi.GENRE_ID " +
                "GROUP BY f.FILM_ID;";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, sql)));
    }

    @Override
    public Film create(Film film) {
        validateFilmGenresAndMpa(film);

        String sql = "INSERT INTO FILMS (FILM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());

        long filmId = jdbcTemplate.queryForObject("SELECT FILM_ID FROM FILMS ORDER BY FILM_ID DESC LIMIT 1", Long.class);
        film.setId(filmId);

        insertFilmGenres(film);

        log.info("Film created: {}", film);
        return film;
    }

    @Override
    public void update(Film film) {
        validateFilmGenresAndMpa(film);

        int rowsUpdated = jdbcTemplate.update("UPDATE FILMS SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? WHERE FILM_ID = ?",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        if (rowsUpdated == 0) {
            log.warn("Film with ID {} not found for update", film.getId());
            throw new FilmNotFoundException("Фильм с ID " + film.getId() + " не найден для обновления.");
        }

        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID = ?", film.getId());
        insertFilmGenres(film);

        log.info("Film updated: {}", film);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID = ?", id);
        jdbcTemplate.update("DELETE FROM FILMS WHERE FILM_ID = ?", id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        validateFilmAndUser(filmId, userId);
        String sql = "INSERT INTO FILM_LIKES (USER_ID, FILM_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, filmId);
        log.info("User with ID {} liked film with ID {}", userId, filmId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        validateFilmAndUser(filmId, userId);
        String sql = "DELETE FROM FILM_LIKES WHERE USER_ID = ? AND FILM_ID = ?";
        jdbcTemplate.update(sql, userId, filmId);
        log.info("User with ID {} unliked film with ID {}", userId, filmId);
    }

    @Override
    public int getNumberOfLikes(Long filmId) {
        String sql = "SELECT COUNT(*) FROM FILM_LIKES WHERE FILM_ID = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, filmId);
    }

    private void validateFilmGenresAndMpa(Film film) {
        if (film.getMpa() == null || !mpaExists(film.getMpa().getId())) {
            throw new MpaCreationException("Указан некорректный MPA.");
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (!genreExists(genre.getId())) {
                    throw new GenreCreationException("Жанр с ID " + genre.getId() + " не существует.");
                }
            }
        }
    }

    private void validateFilmAndUser(long filmId, long userId) {
        getById(filmId);
        String sql = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ?";
        int userExists = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        if (userExists == 0) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден.");
        }
    }

    private void insertFilmGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }
    }

    private boolean mpaExists(int mpaId) {
        String sql = "SELECT COUNT(*) FROM FILM_RATING WHERE RATING_ID = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, mpaId) > 0;
    }

    private boolean genreExists(int genreId) {
        String sql = "SELECT COUNT(*) FROM GENRE_INFO WHERE GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, genreId) > 0;
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

        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("rating_id"), rs.getString("rating")))
                .genres(genres)
                .build();
    }
}
