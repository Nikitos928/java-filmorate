package ru.yandex.practicum.filmorate.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Integer, User> users = new HashMap<>();
    private int generatedId;

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        log.info("Получен запрос: add-user");
        if (user.getName().contains(" ")) {
            log.info("Name содержит пробел");
            throw new ValidationException("Name не может содержать пробел");
        }
        if (user.getLogin() == null) {
            user.setLogin(user.getName());
        }
        user.setId(generatedId);
        users.put(generatedId, user);
        generatedId++;
        return user;
    }

    @PutMapping(value = "/users")
    public User updateUser(@RequestBody User user) {
        log.info("Получен запрос: update-user");
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping("/users")
    public List<User> getUsers() {

        return new ArrayList<>(users.values());
    }

}
