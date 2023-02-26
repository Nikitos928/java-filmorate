package ru.yandex.practicum.filmorate.storage;

import Mapper.FilmMapper;
import Mapper.SetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Qualifier("InBbFilmStorage")
public class FilmDbStorage implements FilmStorage {

    private Long id = 1L;

    private Long idForLike = 1L;

    private final JdbcTemplate jdbcTemplate;

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

        film.setId(id);
        jdbcTemplate.update("INSERT INTO FILMS VALUES (?,?,?,?,?,?,?)",
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getRate());
        id++;

        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }

        if (!film.getGenres().isEmpty()) {
            for (Mpa filmGenre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO GENRE_FILMS VALUES (?,?)", film.getId(), filmGenre.getId());
            }
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {

        jdbcTemplate.update("DELETE FROM LIKE_FILM WHERE FILM_ID = ?", film.getId());

        for (Long like : film.getWhoLikedUserIds()) {
            jdbcTemplate.update("INSERT INTO LIKE_FILM VALUES (?,?,?)", idForLike, film.getId(), like);
            idForLike++;
        }


        jdbcTemplate.update("DELETE FROM GENRE_FILMS WHERE ID_FILM = ?", film.getId());

        if (film.getGenres() != null) {
            for (Mpa filmGenre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO GENRE_FILMS VALUES (?,?)", film.getId(), filmGenre.getId());
            }
        }


        jdbcTemplate.update("UPDATE FILMS SET NAME=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=?, RATING=? , RATE = ?  WHERE ID = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getRate(),
                film.getId());
        if (film.getGenres() != null) {
            for (Mpa filmGenre : film.getGenres()) {
                filmGenre.setName(genre.get(filmGenre.getId() - 1));
            }
            film.setGenres(film.getGenres().stream().sorted(Comparator.comparing(Mpa::getId)).collect(Collectors.toSet()));
        }


        return film;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> films = new ArrayList<>(jdbcTemplate.query("SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING, RATE FROM FILMS", new FilmMapper()));

        for (Film film : films) {

            film.setWhoLikedUserIds((Set<Long>) jdbcTemplate.query("SELECT USER_ID ID FROM LIKE_FILM WHERE FILM_ID=?", new Object[]{film.getId()}, new SetMapper()).stream().collect(Collectors.toSet()));

            for (Object o : jdbcTemplate.query("SELECT ID_GENRE ID FROM GENRE_FILMS WHERE ID_FILM = ?", new Object[]{film.getId()}, new SetMapper())) {
                Set<Mpa> genres = new HashSet<>();
                genres.add(new Mpa(Integer.parseInt(o.toString()), genre.get(Integer.parseInt(o.toString()) - 1)));
                film.setGenres(genres);

            }
        }

        for (Film film : films) {
            film.setMpa(new Mpa(film.getMpa().getId(), rating.get(film.getMpa().getId() - 1)));
            if (film.getGenres() == null) {
                film.setGenres(new HashSet<>());

            }
        }

        return films;
    }

    @Override
    public Film getFilm(Long id) {

        Film film = (Film) jdbcTemplate.query("SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING, RATE FROM FILMS WHERE ID = ?",
                new Object[]{id}, new FilmMapper()).stream().findAny().orElse(null);

        film.setWhoLikedUserIds((Set<Long>) jdbcTemplate.query("SELECT USER_ID ID FROM LIKE_FILM WHERE FILM_ID=?", new Object[]{id}, new SetMapper()).stream().collect(Collectors.toSet()));

        Set<Mpa> genres = new HashSet<>();

        System.out.println(jdbcTemplate.query("SELECT ID_GENRE ID FROM GENRE_FILMS WHERE ID_FILM = ?", new Object[]{id}, new SetMapper()));

        for (Object o : jdbcTemplate.query("SELECT ID_GENRE ID FROM GENRE_FILMS WHERE ID_FILM = ?", new Object[]{id}, new SetMapper())) {
            genres.add(new Mpa(Integer.parseInt(o.toString()), genre.get(Integer.parseInt(o.toString()) - 1)));
        }

        film.setGenres(genres);

        film.setMpa(new Mpa(film.getMpa().getId(), rating.get(film.getMpa().getId() - 1)));
        film.setGenres(film.getGenres().stream().sorted(Comparator.comparing(Mpa::getId)).collect(Collectors.toSet()));
        return film;
    }

}
