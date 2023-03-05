package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<Long, User> users = new HashMap<>();
    private Long generatedId = 1L;

    @Override
    public User addUser(User user) {
        log.info("Получен запрос: add-user");
        user.setId(generatedId);
        users.put(generatedId, user);
        generatedId++;
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.info("Получен запрос: update-user");
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getFriends(Long id) {
        return getUser(id).getFriendIds().stream().map(this::getUser).collect(Collectors.toList());
    }

    @Override
    public List<User> mutualFriends(Long userId1, Long userId2) {
        return getUser(userId1).getFriendIds()
                .stream()
                .filter(getUser(userId2).getFriendIds()::contains)
                .map(this::getUser)
                .collect(Collectors.toList());
    }

}
