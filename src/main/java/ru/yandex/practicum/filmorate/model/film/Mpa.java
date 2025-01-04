package ru.yandex.practicum.filmorate.model.film;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Mpa {

    private int id;
    @NotBlank
    private String name;

    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }
}