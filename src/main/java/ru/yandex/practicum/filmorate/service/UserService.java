package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addUser(User user) throws ValidationException {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws ValidationException {
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }


    public User getUser(Long id) {
        checkId(id);
        return userStorage.getUser(id);
    }

    public User addFriend(Long userId, Long friendId) {
        checkId(userId);
        if (0 > friendId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());

        log.info("Пользователь c ID:" + user.getId() + " добавил пользователя с ID:" + friend.getId() + " в друзья");
        return user;
    }

    public User deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
        log.info("Пользователь c ID:" + user.getId() + " удалил пользователя с ID:" + friend.getId() + " из друзей");
        return user;
    }

    public List<User> mutualFriends(Long userId1, Long userId2) {
        User user1 = userStorage.getUser(userId1);
        User user2 = userStorage.getUser(userId2);
        return user1.getFriends()
                .stream()
                .filter(user2.getFriends()::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getFriends(Long id) {
        User user = userStorage.getUser(id);
        return user.getFriends().stream().map(userStorage::getUser).collect(Collectors.toList());
    }

    private void checkId(Long id) {
        if (id < 1) {
            throw new IllegalArgumentException("ID не может быть отрицательным");
        }
        if (userStorage.getUser(id) == null) {
            throw new UserNotFoundException(String.format(
                    "Пользователь %s не найден",
                    id));
        }
    }
}
