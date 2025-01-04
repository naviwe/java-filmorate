package ru.yandex.practicum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class Genre {

    private int id;
    @NotBlank
    private String name;

    public Genre(int id, String name) {
        this.name = name;
        this.id = id;
    }
}