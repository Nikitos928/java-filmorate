package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<Long, User> users = new HashMap<>();
    private Long generatedId = 1L;


    public User addUser(User user) throws ValidationException {
        log.info("Получен запрос: add-user");
        checkWhitespace(user);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(generatedId);
        users.put(generatedId, user);
        generatedId++;
        return user;
    }


    public User updateUser(User user) throws ValidationException {
        log.info("Получен запрос: update-user");
        checkWhitespace(user);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("Пользователя с таким id нет");
        }
    }


    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUser(Long id) {
        return users.get(id);
    }
    private void checkWhitespace(User user) throws ValidationException {
        if (user.getLogin().contains(" ")) {
            log.info("Login содержит пробел");
            throw new ValidationException("Name содержит пробел");
        }
    }
}
