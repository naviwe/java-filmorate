package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GenreCreationException extends RuntimeException {
    public GenreCreationException(String message) {
        super(message);
    }
}
