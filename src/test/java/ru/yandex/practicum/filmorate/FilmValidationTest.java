package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmValidationTest {

    @Test
    public void testFilmValidation_validFilm() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        assertDoesNotThrow(() -> validateFilm(film));
    }

    @Test
    public void testFilmValidation_invalidName() {
        Film film = new Film();
        film.setName("");

        ValidationException exception = assertThrows(ValidationException.class, () -> validateFilm(film));
        assertEquals("Название фильма не может быть пустым.", exception.getMessage());
    }

    @Test
    public void testFilmValidation_invalidDescription() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("A".repeat(201));

        ValidationException exception = assertThrows(ValidationException.class, () -> validateFilm(film));
        assertEquals("Описание фильма не может превышать 200 символов.", exception.getMessage());
    }

    @Test
    public void testFilmValidation_invalidReleaseDate() {
        Film film = new Film();
        film.setName("Test Film");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> validateFilm(film));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    public void testFilmValidation_invalidDuration() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDuration(0);

        ValidationException exception = assertThrows(ValidationException.class, () -> validateFilm(film));
        assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может превышать 200 символов.");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }
}
