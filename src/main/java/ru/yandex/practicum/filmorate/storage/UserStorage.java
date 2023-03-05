package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    public User addUser(User user);

    public User updateUser(User user);

    public List<User> getUsers();

    public User getUser(Long id);

    public List<User> getFriends(Long id);

    public List<User> mutualFriends(Long userId1, Long userId2);
}
