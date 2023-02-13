package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
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
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь %s не найден",
                    userId));
        }
        checkId(filmId);
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        film.getWhoLikedUserIds().remove(user.getId());
        return film;
    }

    public List<Film> getPopularFilms(Long cout) {
        return filmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparing(film -> film.getWhoLikedUserIds().size() * -1))
                .limit(cout)
                .collect(Collectors.toList());
    }


    private void checkId(Long id) {
        if (id < 1 || filmStorage.getFilm(id) == null) {
            throw new NotFoundException(String.format(
                    "Фильм %s не найден",
                    id));
        }
    }

    private void checkData(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата не соответствует параметрам");
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
    }

}
