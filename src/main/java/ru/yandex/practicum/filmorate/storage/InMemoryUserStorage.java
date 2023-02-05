package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
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


    public User addUser(User user) {
        log.info("Получен запрос: add-user");

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(generatedId);
        users.put(generatedId, user);
        generatedId++;
        return user;
    }


    public User updateUser(User user) {
        log.info("Получен запрос: update-user");
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }


    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUser(Long id) {
        return users.get(id);
    }

}