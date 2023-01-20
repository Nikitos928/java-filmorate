package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Integer, Film> films = new HashMap<>();
    protected int generatedId = 1;

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.info("Получен запрос: add-film");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата не соответствует параметрам");
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        film.setId(generatedId);
        films.put(generatedId, film);
        generatedId++;
        return film;
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        log.info("Получен запрос: update-film");
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Фильма с таки id нет");
        }
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Получен запрос: get-films");
        return new ArrayList<>(films.values());
    }

}