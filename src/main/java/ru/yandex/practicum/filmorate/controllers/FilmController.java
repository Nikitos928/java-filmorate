package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class FilmController {

    private final FilmService filmService;

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmService.updateFilm(film);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable(value = "id") Long id) {
        return filmService.getFilm(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike(@PathVariable(value = "id") Long filmId, @PathVariable(value = "userId") Long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable(value = "id") Long filmId, @PathVariable(value = "userId") Long userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Long count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable(value = "id") Integer genreId) throws ValidationException {
        return filmService.getGenre(genreId);
    }

    @GetMapping("/mpa")
    public List<Mpa> getMpa() {
        return filmService.getRatings();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpa(@PathVariable(value = "id") Integer mpaId) throws ValidationException {
        return filmService.getRating(mpaId);
    }

}