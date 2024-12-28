package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenresDbStorage implements GenresStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT GI.GENRE_ID , GI.GENRE_NAME  FROM GENRE_INFO gi ORDER BY GI.GENRE_ID ;";
        return new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs)));
    }

    @Override
    public Genre getGenreById(int id) {
        String sql = "SELECT GI.GENRE_ID, GI.GENRE_NAME FROM GENRE_INFO gi WHERE GI.GENRE_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Genre with ID {} not found", id);
            throw new NotFoundException("Жанр с ID " + id + " не найден.");
        }
    }


    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}