package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.controllers.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    public User addUser(User user) throws ValidationException;

    public User updateUser(User user) throws ValidationException;

    public List<User> getUsers();

    public User getUser(Long id);

}
