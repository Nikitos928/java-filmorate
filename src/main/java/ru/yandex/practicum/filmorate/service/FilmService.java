package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RestController
public class FilmService {

    private final String f = "InDbFilmStorage";

    private final String u = "InDbUserStorage";

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier(f) FilmStorage filmStorage, @Qualifier(u) UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmStorage.getGenres();
    }


    public Film addFilm(Film film) throws ValidationException {
        checkData(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException {
        checkData(film);
        checkId(film.getId());
        if (filmStorage.getFilm(film.getId()) != null) {
            return filmStorage.updateFilm(film);
        } else {
            throw new ValidationException("Фильма с таки id нет");
        }
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(Long id) {
        checkId(id);
        return filmStorage.getFilm(id);
    }

    public Film addLike(Long filmId, Long userId) {
        checkId(filmId);
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        film.getWhoLikedUserIds().add(user.getId());
        log.info("Пользователь с ID: " + userId + "поставил Like фильму с ID: " + filmId);
        filmStorage.updateFilm(film);
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        if (userId < 0) {
            throw new NotFoundException(String.format("ID не может быть меньше 0"));
        }
        if (userStorage.getUser(userId) == null || userId < 0 || userStorage.getUsers().size() < userId) {
            throw new NotFoundException(String.format("Пользователь %s не найден", userId));
        }
        checkId(filmId);
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        film.getWhoLikedUserIds().remove(user.getId());
        filmStorage.updateFilm(film);
        return film;
    }

    public List<Film> getPopularFilms(Long cout) {
        return filmStorage.getFilms().stream().sorted(Comparator.comparing(film -> film.getWhoLikedUserIds().size() * -1)).limit(cout).collect(Collectors.toList());
    }


    private void checkId(Long id) {
        if (id < 1 || filmStorage.getFilms().size() < id) {
            throw new NotFoundException(String.format("Фильм %s не найден", id));
        }
    }

    private void checkData(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата не соответствует параметрам");
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
    }

    public List<Genre> getGenre() {
        return filmStorage.getGenres();
    }

    public Genre getGenreId(Integer id) throws ValidationException {
        if (filmStorage.getRating().size() < id || id < 0) {
            throw new NotFoundException("Такого жанра пока нет");
        }
        return filmStorage.getGenres().get(id - 1);
    }

    public List<Genre> getRatings() {
        return filmStorage.getRating();
    }

    public Genre getRating(Integer id) throws ValidationException {
        if (filmStorage.getRating().size() < id || id < 0) {
            throw new NotFoundException("Такого рейтинга пока нет");
        }
        return filmStorage.getRating().get(id - 1);
    }

}
