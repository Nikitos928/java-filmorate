package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.mapper.SetMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Qualifier("InDbFilmStorage")
public class FilmDbStorage implements FilmStorage {

    private Long id = 1L;

    private Long idForLike = 1L;
    private final JdbcTemplate jdbcTemplate;

    private List<String> genre;

    @Override
    public List<Genre> getGenres() {

        genre = new ArrayList<>(jdbcTemplate.query("SELECT NAME FROM GENRE", (rs, rowNom) -> rs.getString("NAME")));
        System.out.println();
        List<Genre> list = new ArrayList<>();
        int i = 1;
        for (String s : genre) {
            list.add(new Genre(i, s));
            i++;
        }
        return list;
    }

    @Override
    public List<Mpa> getRating() {
        List<String> rating = new ArrayList<>(jdbcTemplate.query("SELECT NAME FROM RATING", (rs, rowNom) -> rs.getString("NAME")));
        List<Mpa> list = new ArrayList<>();
        int i = 1;
        for (String s : rating) {
            list.add(new Mpa(i, s));
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
            for (Genre filmGenre : film.getGenres()) {
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
            for (Genre filmGenre : film.getGenres()) {
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
            for (Genre filmGenre : film.getGenres()) {
                filmGenre.setName(genre.get(filmGenre.getId() - 1));
            }
            film.setGenres(film.getGenres().stream().sorted(Comparator.comparing(Genre::getId)).collect(Collectors.toSet()));
        }


        return film;
    }

    @Override
    public List<Film> getFilms(Long limit) {

        Map<Integer, GenreAndFriends> mapGenre = new HashMap<>();

        List<Film> films;

        if (limit == 0L) {
            films = new ArrayList<>(jdbcTemplate.query("SELECT F.ID id_film, " +
                    "F.NAME name, " +
                    "F.DESCRIPTION description, " +
                    "F.RELEASE_DATE release_date, " +
                    "F.DURATION duration, " +
                    "R.ID id_rating, " +
                    "R.NAME rating, " +
                    "F.RATE rate " +
                    "FROM FILMS AS F " +
                    "INNER JOIN RATING AS R ON F.RATING = R.ID ", new FilmMapper()));

        } else {
            films = new ArrayList<>(jdbcTemplate.query("SELECT F.ID id_film, " +
                    "F.NAME name, " +
                    "F.DESCRIPTION description, " +
                    "F.RELEASE_DATE release_date, " +
                    "F.DURATION duration, " +
                    "R.ID id_rating, " +
                    "R.NAME rating, " +
                    "F.RATE rate, " +
                    "COUNT (L.USER_ID) " +
                    "FROM FILMS AS F " +
                    "LEFT JOIN LIKE_FILM AS L ON F.ID = L.FILM_ID " +
                    "INNER JOIN RATING AS R ON F.RATING = R.ID " +
                    "GROUP BY F.ID " +
                    "ORDER BY COUNT (L.USER_ID) DESC, F.ID  " +
                    "LIMIT ?", new FilmMapper(), limit));
        }
        List<GenreMap> genreForMap = new ArrayList<>(
                jdbcTemplate.query("SELECT F.ID film_id, G.ID genre_id, G.NAME genre_name " +
                        "FROM FILMS AS F " +
                        "INNER JOIN GENRE_FILMS AS GF ON F.ID = GF.ID_FILM " +
                        "INNER JOIN GENRE AS G ON GF.ID_GENRE = G.ID", new GenreMapper()));

        List<Like> like = new ArrayList<>(jdbcTemplate.query("SELECT * FROM LIKE_FILM", (rs, rowNom) ->
                new Like(rs.getLong("FILM_ID"), rs.getLong("USER_ID"))));

        for (Like like1 : like) {
            if (mapGenre.containsKey(like1.getLike1().intValue())) {
                mapGenre.get(like1.getLike1().intValue()).getSetLikesInMap().add(like1.getLike2());
            } else {
                Set<Long> likesSet = new HashSet<>();
                likesSet.add(like1.getLike2());
                GenreAndFriends genreAndFriends = new GenreAndFriends();
                genreAndFriends.setSetLikesInMap(likesSet);
                mapGenre.put(like1.getLike1().intValue(), genreAndFriends);
            }
        }

        for (GenreMap genreMap : genreForMap) {
            if (mapGenre.containsKey(genreMap.getFilmId())) {
                mapGenre.get(genreMap.getFilmId()).getSetGenresInMap()
                        .add(new Genre(genreMap.getGenreId(), genreMap.getGenre()));
            } else {
                HashSet<Genre> genres = new HashSet<>();
                genres.add(new Genre(genreMap.getGenreId(), genreMap.getGenre()));
                GenreAndFriends genreAndFriends = new GenreAndFriends();
                genreAndFriends.setSetGenresInMap(genres);
                mapGenre.put(genreMap.getFilmId(), genreAndFriends);
            }
        }

        for (Film film : films) {
            if (mapGenre.containsKey((int) (long) film.getId())) {
                film.setGenres(mapGenre.get((int) (long) film.getId()).getSetGenresInMap());
                film.setWhoLikedUserIds(mapGenre.get(film.getId().intValue()).getSetLikesInMap());
            }
        }
        return films;
    }


    @Override
    public Film getFilm(Long id) {

        Film film = (Film) jdbcTemplate.query("SELECT F.ID id_film, " +
                        "F.NAME name, " +
                        "F.DESCRIPTION description, " +
                        "F.RELEASE_DATE release_date, " +
                        "F.DURATION duration, " +
                        "R.ID id_rating, " +
                        "R.NAME rating, " +
                        "F.RATE rate " +
                        "FROM FILMS AS F " +
                        "INNER JOIN RATING AS R ON F.RATING = R.ID " +
                        "WHERE F.ID = ?",
                new FilmMapper(), id).stream().findAny().orElse(null);

        film.setWhoLikedUserIds((Set<Long>) jdbcTemplate.query("SELECT USER_ID ID " +
                        "FROM LIKE_FILM WHERE FILM_ID=?",
                new SetMapper(), id).stream().collect(Collectors.toSet()));

        Set<Genre> genres = new HashSet<>();

        for (Object o : jdbcTemplate.query("SELECT ID_GENRE ID FROM GENRE_FILMS WHERE ID_FILM = ?",
                new SetMapper(), id)) {
            genres.add(new Genre(Integer.parseInt(o.toString()), genre.get(Integer.parseInt(o.toString()) - 1)));
        }

        film.setGenres(genres);

        film.setGenres(film.getGenres().stream().sorted(Comparator.comparing(Genre::getId)).collect(Collectors.toSet()));
        return film;
    }

}
