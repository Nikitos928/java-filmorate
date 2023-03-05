package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {

    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public List<Film> getFilms(Long limit);

    public Film getFilm(Long id);

    public List<Genre> getGenres();

    public List<Mpa> getRating();

}
