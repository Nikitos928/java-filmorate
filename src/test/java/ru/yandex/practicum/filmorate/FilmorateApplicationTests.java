package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.controllers.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class FilmorateApplicationTests {

    FilmController filmController;
    UserController userController;

    private Validator validator;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validationUserDateTest() {
        User user = User.builder()
                .birthday(LocalDate.of(2030, 12, 12))
                .email("tttt@yandex.ru")
                .login("Login")
                .name("Name")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationUserEmailTest() {
        User user = User.builder()
                .birthday(LocalDate.of(2000, 12, 12))
                .email("непочта")
                .login("Login")
                .name("Name")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationUserLoginTest() {
        User user = User.builder()
                .birthday(LocalDate.of(2000, 12, 12))
                .email("tttt@yandex.ru")
                .login("")
                .name("Name")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationUserLoginNullTest() {
        User user = User.builder()
                .birthday(LocalDate.of(2000, 12, 12))
                .email("tttt@yandex.ru")
                .login(null)
                .name("Name")
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationFilmNameEmptyTest() {
        Film film = Film.builder()
                .name("")
                .description("Description")
                .duration(30)
                .releaseDate(LocalDate.of(2000, 12, 12)).build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationFilmNameNullTest() {
        Film film = Film.builder()
                .name(null)
                .description("Description")
                .duration(30)
                .releaseDate(LocalDate.of(2000, 12, 12)).build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println(violations);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationFilmDescriptionSizeTest() {
        String descriptionSize201 = "a" + "a".repeat(200);

        Film film = Film.builder()
                .name("Name")
                .description(descriptionSize201)
                .duration(30)
                .releaseDate(LocalDate.of(2000, 12, 12)).build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println(violations);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationFilmDurationPositiveTest() {
        Film film = Film.builder()
                .name("Name")
                .description("description")
                .duration(-30)
                .releaseDate(LocalDate.of(2000, 12, 12)).build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println(violations);
        assertEquals(1, violations.size());
    }


    @Test
    void addUpdateGetFilmValidationExceptionTest() throws ValidationException {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .duration(30)
                .releaseDate(LocalDate.of(2000, 12, 12)).build();

        filmController = new FilmController();
        filmController.addFilm(film);
        assertEquals(film, filmController.getFilms().get(0));

        Film film1 = Film.builder().id(1)
                .name("Name")
                .description("Description")
                .duration(50)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();
        filmController.updateFilm(film1);
        assertEquals(film1, filmController.getFilms().get(0));

        Film film2 = Film.builder()
                .name("Name")
                .description("Description")
                .duration(50)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        filmController.addFilm(film2);

        assertEquals(2, filmController.getFilms().size());
        assertEquals(film2, filmController.getFilms().get(1));
        assertEquals(film1, filmController.getFilms().get(0));

        Film film3 = Film.builder()
                .id(10)
                .name("Name")
                .description("Description")
                .duration(50)
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.updateFilm(film3));


        Film film4 = Film.builder()
                .name("Name")
                .description("Description")
                .duration(30)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film4));

        Film film5 = Film.builder()
                .name("Name")
                .description("Description")
                .duration(30)
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build();

        Assertions.assertThrows(ValidationException.class, () -> filmController.updateFilm(film5));


    }

    @Test
    void addUpdateGetUserValidationExceptionTest() throws ValidationException {
        userController = new UserController();
        User user = User.builder()
                .birthday(LocalDate.of(2000, 12, 12))
                .email("tttt@yandex.ru")
                .login("Login")
                .name("Name")
                .build();

        userController.addUser(user);

        assertEquals(user, userController.getUsers().get(0));

        User user1 = User.builder()
                .id(1)
                .birthday(LocalDate.of(2000, 12, 12))
                .email("tt@yandex.ru")
                .login("Login")
                .name("Name")
                .build();

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

        User user3 = User.builder()
                .id(99)
                .birthday(LocalDate.of(2000, 12, 12))
                .email("tt@yandex.ru")
                .login("Login")
                .name("Name").build();

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(user3));

        User user4 = User.builder()
                .birthday(LocalDate.of(2000, 12, 12))
                .email("tt@yandex.ru")
                .login("Lo gin")
                .name("Name").build();

        Assertions.assertThrows(ValidationException.class, () -> userController.updateUser(user4));

        Assertions.assertThrows(ValidationException.class, () -> userController.addUser(user4));

        User user5 = User.builder()
                .birthday(LocalDate.of(2000, 12, 12))
                .email("tt@yandex.ru")
                .login("newLogin")
                .build();
        userController.addUser(user5);
        assertEquals(user5.getName(), "newLogin");

    }

}




