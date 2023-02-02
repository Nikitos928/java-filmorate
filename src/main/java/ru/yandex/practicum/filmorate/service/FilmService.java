package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;


    public Film addFilm(Film film) throws ValidationException {
    return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException{
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms(){
        return filmStorage.getFilms();
    }

    public Film getFilm(Long id){
        if (filmStorage.getFilm(id) == null) {
            throw new UserNotFoundException(String.format(
                    "Пользователь %s не найден",
                    id));
        }
        return filmStorage.getFilm(id);
    }

    public Film addLike (Long filmId, Long userId){
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        film.getLike().add(user.getId());
        log.info("Пользователь с ID: " + userId + "поставил Like фильму с ID: " + filmId);
        return film;
    }

    public Film deleteLike (Long filmId, Long userId){
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        film.getLike().remove(user.getId());
        return film;
    }

    public List<Film> getPopularFilms(Long cout){
        return filmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparing(film -> film.getLike().size()*-1))
                .limit(cout)
                .collect(Collectors.toList());
    }

}
