package ru.yandex.practicum.filmorate;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.controllers.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class FilmorateApplicationTests {

    FilmController filmController;
    UserController userController;

    @Test
    void contextLoads() {
    }

    @Test
    void addUpdateGetFilmTest() throws ValidationException {
        Film film = Film.builder().name("Name")
                .description("Description")
                .duration(30)
                .releaseDate(LocalDate.of(2000, 12, 12)).build();

        filmController = new FilmController();
        filmController.addFilm(film);
        assertEquals(film, filmController.getFilms().get(0));

        Film film1 = Film.builder().id(0)
                .name("Name")
                .description("Description")
                .duration(50)
                .releaseDate(LocalDate.of(2000, 12, 12)).build();
        filmController.updateFilm(film1);
        assertEquals(film1, filmController.getFilms().get(0));

        Film film2 = Film.builder()
                .name("Name")
                .description("Description")
                .duration(50)
                .releaseDate(LocalDate.of(2000, 12, 12)).build();

        filmController.addFilm(film2);

        assertEquals(2, filmController.getFilms().size());
        assertEquals(film2, filmController.getFilms().get(1));
        assertEquals(film1, filmController.getFilms().get(0));
    }
    @Test
    void addUpdateGetUserTest() throws ValidationException {
        userController = new UserController();
        User user = User.builder()
                .birthday(LocalDate.of(2000, 12, 12))
                .email("tttt@yandex.ru")
                .login("Login")
                .name("Name").build();

        userController.addUser(user);

        assertEquals(user, userController.getUsers().get(0));

        User user1 = User.builder()
                .birthday(LocalDate.of(2000, 12, 12))
                .email("tt@yandex.ru")
                .login("Login")
                .name("Name").build();

        userController.updateUser(user1);
        assertEquals(user1, userController.getUsers().get(0));
        assertEquals(1, userController.getUsers().size());

        User user2 = User.builder()
                .birthday(LocalDate.of(2001, 12, 12))
                .email("yyyy@yandex.ru")
                .login("Login1")
                .name("Name1").build();
        userController.addUser(user2);
        assertEquals(user2, userController.getUsers().get(1));
        assertEquals(2, userController.getUsers().size());
    }

    @Test
    void ExceptionNullUserFilmsTest() throws ValidationException {
        filmController = new FilmController();
        userController = new UserController();
        Film film = Film.builder().id(0)
                .name("Name")
                .description("Description")
                .duration(50)
                .releaseDate(LocalDate.of(1894, 12, 28)).build();

        User user = User.builder()
                .birthday(LocalDate.of(2000, 12, 12))
                .email("tttt@yandex.ru")
                .login("Login")
                .name("Na me").build();


        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user));

        film.setName(null);
        film.setReleaseDate(LocalDate.of(2000, 12, 28));
        filmController.addFilm(film);
    }




}
