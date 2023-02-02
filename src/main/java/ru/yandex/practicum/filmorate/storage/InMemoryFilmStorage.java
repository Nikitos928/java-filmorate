package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<Long, Film> films = new HashMap<>();
    private Long generatedId = 1L;


    public Film addFilm(Film film) throws ValidationException {
        log.info("Получен запрос: add-film");
        checkData(film);
        film.setId(generatedId);
        films.put(generatedId, film);
        generatedId++;
        return film;
    }


    public Film updateFilm(Film film) throws ValidationException {
        log.info("Получен запрос: update-film");
        checkData(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Фильма с таки id нет");
        }
    }


    public List<Film> getFilms() {
        log.info("Получен запрос: get-films");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(Long id) {
        return films.get(id);
    }

    public Film getFilm(Integer id){
        return films.get(id);
    }


    private void checkData(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата не соответствует параметрам");
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
    }
}
