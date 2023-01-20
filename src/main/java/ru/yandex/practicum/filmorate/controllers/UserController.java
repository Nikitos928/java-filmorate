package ru.yandex.practicum.filmorate.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Integer, User> users = new HashMap<>();
    private int generatedId = 1;

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) {
        log.info("Получен запрос: add-user");
        if (user.getLogin().contains(" ")) {
            log.info("Name содержит пробел");
            user.setLogin(user.getLogin().replace(" ", ""));
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(generatedId);
        users.put(generatedId, user);
        generatedId++;
        return user;
    }

    @PutMapping(value = "/users")
    public User updateUser(@RequestBody User user) throws ValidationException {
        log.info("Получен запрос: update-user");
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("Пользователя с таки id нет");
        }
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

}
