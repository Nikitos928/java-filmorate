package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.controllers.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    public Film addFilm(Film film) throws ValidationException;

    public Film updateFilm(Film film) throws ValidationException;

    public List<Film> getFilms();

    public Film getFilm(Long id);

}
