package ru.yandex.practicum.filmorate.model.film;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@Data
public class Film {
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotNull(message = "Описание не может быть пустым")
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    Set<Long> likes;

    private final Set<Long> usersIdsLiked = new HashSet<>();

    @NotNull
    Mpa mpa;
    LinkedHashSet<Genre> genres;

    public void addLike(Long userId) {
        usersIdsLiked.add(userId);
    }

    public void removeLike(Long userId) {
        usersIdsLiked.remove(userId);
    }

}
