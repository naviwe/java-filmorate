package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.service.film.MpaService;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> getAllMpa() {
        log.info("Request received: GET /mpa");
        List<Mpa> mpaList = mpaService.getAllMpa();
        log.info("Response: {} MPA ratings found", mpaList.size());
        return mpaList;
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@Valid @PathVariable long id) throws NotFoundException {
        log.info("Request received: GET /mpa/{}", id);
        Mpa mpa = mpaService.getMpaById((int) id);
        log.info("Response: MPA rating found - {}", mpa);
        return mpa;
    }
}
