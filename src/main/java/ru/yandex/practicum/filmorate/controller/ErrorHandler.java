package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(FilmNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleFilmNotFoundException(FilmNotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Film not found");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UserNotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "User not found");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Not Found");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation error");
        response.put("message", e.getMessage());
        response.put("code", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Validation failed");
        response.put("message", e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", ")));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MpaCreationException.class)
    public ResponseEntity<Map<String, String>> handleMpaCreationException(MpaCreationException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "MPA creation failed");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(GenreCreationException.class)
    public ResponseEntity<Map<String, String>> handleGenreCreationException(GenreCreationException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Genre creation failed");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal server error");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
