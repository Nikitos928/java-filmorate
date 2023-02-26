package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Service
@Qualifier("InMemory")
public class InMemoryFilmStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<Long, Film> films = new HashMap<>();
    private Long generatedId = 1L;
    private final List<String> genre = Arrays.asList("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    private final List<String> rating = Arrays.asList("G", "PG", "PG-13", "R", "NC-17");

    public List<Genre> getGenres() {
        List<Genre> list = new ArrayList<>();
        int i = 1;
        for (String s : genre) {
            list.add(new Genre(i, s));
            i++;
        }
        return list;
    }

    public List<Genre> getRating() {
        List<Genre> list = new ArrayList<>();
        int i = 1;
        for (String s : rating) {
            list.add(new Genre(i, s));
            i++;
        }
        return list;
    }

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
