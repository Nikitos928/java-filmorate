package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmoRateFilmApplicationTestsDb {


    private final FilmDbStorage filmStorage;

    @Test
    public void getFilmTest() {

        Film film1 = Film.builder()
                .name("Фильм")
                .description("Description")
                .duration(30)
                .mpa(new Mpa(3, "PG-13"))
                .whoLikedUserIds(new HashSet<>())
                .rate(0)
                .releaseDate(LocalDate.of(2000, 12, 12)).build();

        filmStorage.addFilm(film1);

        Optional<Film> userOptional = Optional.ofNullable(filmStorage.getFilm(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Фильм")
                );

        Film film2 = Film.builder()
                .name("Фильм1")
                .description("Description1")
                .duration(30)
                .mpa(new Mpa(3, "PG-13"))
                .whoLikedUserIds(new HashSet<>())
                .rate(0)
                .releaseDate(LocalDate.of(2000, 12, 12)).build();

        filmStorage.addFilm(film2);

        Optional<List<Film>> userOptional1 = Optional.ofNullable(filmStorage.getFilms());

        assertThat(userOptional1).isPresent().hasValueSatisfying(AssertionsForClassTypes::assertThat)
                .hasValue(Arrays.asList(film1, film2));

        film1.setName("НовоеИмя");
        film2.setName("НовоеИмя");
        film1.setDescription("НовоеОписание");
        film2.setDescription("НовоеОписание");

        filmStorage.updateFilm(film1);
        filmStorage.updateFilm(film2);

        Optional<List<Film>> userOptional2 = Optional.ofNullable(filmStorage.getFilms());

        assertThat(userOptional2).isPresent().hasValueSatisfying(AssertionsForClassTypes::assertThat)
                .hasValue(Arrays.asList(film1, film2));


        Optional<List<Genre>> userOptional3 = Optional.ofNullable(filmStorage.getGenres());

        assertThat(userOptional3).isPresent().hasValueSatisfying(AssertionsForClassTypes::assertThat)
                .hasValue(Arrays.asList(new Genre(1, "Комедия"),
                        new Genre(2, "Драма"),
                        new Genre(3, "Мультфильм"),
                        new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")));

        Optional<List<Genre>> userOptional4 = Optional.ofNullable(filmStorage.getRating());

        assertThat(userOptional4).isPresent().hasValueSatisfying(AssertionsForClassTypes::assertThat)
                .hasValue(Arrays.asList(new Genre(1, "G"),
                        new Genre(2, "PG"),
                        new Genre(3, "PG-13"),
                        new Genre(4, "R"),
                        new Genre(5, "NC-17")));


    }
}


