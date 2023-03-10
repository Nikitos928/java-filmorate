package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<Long, Film> films = new HashMap<>();
    private Long generatedId = 1L;

@Override
    public Film addFilm(Film film) {
        log.info("Получен запрос: add-film");
        film.setId(generatedId);
        films.put(generatedId, film);
        generatedId++;
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Получен запрос: update-film");
        films.put(film.getId(), film);
        return film;

    }

    @Override
    public List<Film> getFilms() {
        log.info("Получен запрос: get-films");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(Long id) {
        return films.get(id);
    }

    public Film getFilm(Integer id) {
        return films.get(id);
    }

}
