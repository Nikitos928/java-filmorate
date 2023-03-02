package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RestController
public class UserService {
    private final UserStorage userStorage;

    private final String x = "InDbUserStorage";

    public UserService(@Qualifier(x) UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) throws ValidationException {
        checkWhitespace(user);
        checkName(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws ValidationException {
        if (user == null) {
            throw new ValidationException();
        }
        checkWhitespace(user);
        checkId(user.getId());
        checkName(user);
        return userStorage.updateUser(user);
    }


    public List<User> getUsers() {
        return userStorage.getUsers();
    }


    public User getUser(Long id) throws ValidationException {
        checkId(id);
        return userStorage.getUser(id);
    }

    public User addFriend(Long friendId, Long userId) throws ValidationException {
        if (friendId < 0 || userId < 0) {
            throw new NotFoundException("ID не может быть отрицательным");
        }

        checkId(userId);

        if (friendId.equals(userId)) {
            throw new ValidationException("Самого себя добавить в друзья нельзя");
        }
        if (userStorage.getUser(friendId) == null) {
            throw new NotFoundException("Пользователь с ID: " + friendId + " не найден");
        }

        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("Пользователь c ID: " + userId + " не найден");
        }

        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        friend.getFriendIds().add(userId);
        userStorage.updateUser(friend);

        return user;
    }

    public User deleteFriend(Long userId, Long friendId) {
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь %s не найден",
                    userId));
        }
        if (userStorage.getUser(friendId) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь %s не найден",
                    friendId));
        }
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriendIds().remove(friend.getId());
        friend.getFriendIds().remove(user.getId());
        log.info(String.format("Пользователь c ID: %s удалил пользователя %s из друзей", user.getId(), friend.getId()));


        userStorage.updateUser(user);
        userStorage.updateUser(friend);


        return user;
    }

    public List<User> mutualFriends(Long userId1, Long userId2) {
        User user1 = userStorage.getUser(userId1);
        User user2 = userStorage.getUser(userId2);
        return user1.getFriendIds()
                .stream()
                .filter(user2.getFriendIds()::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.getUser(id);
        return user.getFriendIds().stream().map(userStorage::getUser).collect(Collectors.toList());
    }

    private void checkId(Long id) throws ValidationException {
        if (id < 1) {
            throw new IllegalArgumentException("ID не может быть отрицательным");
        }
        if (userStorage.getUsers().size() < id) {
            throw new NotFoundException(String.format(
                    "Пользователь %s не найден",
                    id));
        }
        if (userStorage.getUser(id) == null) {
            throw new NotFoundException(String.format(
                    "Пользователь %s не найден",
                    id));
        }
    }

    private void checkWhitespace(User user) throws ValidationException {
        if (user.getLogin().contains(" ")) {
            log.info("Login содержит пробел");
            throw new ValidationException("Login содержит пробел");
        }
    }

    private User checkName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            return user;
        }
        return user;
    }
}