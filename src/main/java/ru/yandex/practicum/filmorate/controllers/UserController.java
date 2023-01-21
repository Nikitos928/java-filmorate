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
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();
    private int generatedId = 1;

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        log.info("Получен запрос: add-user");
        whitespaceСheck(user);
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
        whitespaceСheck(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("Пользователя с таким id нет");
        }
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public void whitespaceСheck (User user) throws ValidationException {
            if (user.getLogin().contains(" ")) {
                log.info("Name содержит пробел");
                throw new ValidationException("Name содержит пробел");
            }
    }


}
