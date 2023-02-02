package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@SpringBootApplication
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
    return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
    return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
    return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable(value = "id") Long id){
        return filmService.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable(value = "id") Long filmId, @PathVariable(value = "userId") Long userId){
    return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable(value = "id") Long filmId,@PathVariable(value = "userId") Long userId){

    return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List <Film> getPopularFilms ( @RequestParam(defaultValue = "10", required = false) Long count){
        return filmService.getPopularFilms(count);
    }

}