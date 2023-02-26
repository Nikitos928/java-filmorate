package ru.yandex.practicum.filmorate.storage;

import Mapper.SetMapper;
import Mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Qualifier("InBbUserStorage")
public class UserDbStorage implements UserStorage {

    private Long id = 1L;

    private Long idForFrind = 1L;

    private final JdbcTemplate jdbcTemplate;


    @Override
    public User addUser(User user) {
        user.setId(id);
        jdbcTemplate.update("INSERT INTO USERS VALUES (?,?,?,?,?)",
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        id++;
        return user;
    }

    @Override
    public User updateUser(User user) {

        jdbcTemplate.update("DELETE FROM FRIENDS WHERE USER_1 = ? AND STATUS = 'Подтвержденная'", user.getId());
        jdbcTemplate.update("DELETE FROM FRIENDS WHERE USER_1 = ? AND STATUS = 'Не подтвежденная'", user.getId());

        for (Long friend : user.getFriendRequests()) {
            jdbcTemplate.update("INSERT INTO FRIENDS VALUES (?,?,?,?)", idForFrind, user.getId(), friend, "Не подтвежденная");
            idForFrind++;
        }

        for (Long friend : user.getFriendIds()) {
            jdbcTemplate.update("INSERT INTO FRIENDS VALUES (?,?,?,?)", idForFrind, user.getId(), friend, "Подтвержденная");
            idForFrind++;
        }


        jdbcTemplate.update("UPDATE USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE ID = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()

        );

        return user;
    }

    @Override
    public List<User> getUsers() {
        List<User> friends = new ArrayList<>(jdbcTemplate.query("SELECT * FROM USERS", new UserMapper()));
        for (User friend : friends) {
            friend.setFriendIds((Set<Long>) jdbcTemplate.query("SELECT USER_2 ID " +
                            "FROM FRIENDS " +
                            "WHERE USER_1 = ? AND STATUS = 'Подтвержденная'",
                    new Object[]{friend.getId()}, new SetMapper()).stream().collect(Collectors.toSet()));
        }

        for (User friend : friends) {
            friend.setFriendRequests((Set<Long>) jdbcTemplate.query("SELECT USER_2 ID " +
                            "FROM FRIENDS " +
                            "WHERE USER_1 = ? AND STATUS = 'Не подтвежденная'",
                    new Object[]{friend.getId()}, new SetMapper()).stream().collect(Collectors.toSet()));
        }
        return friends;
    }

    @Override
    public User getUser(Long id) {

        User user = (User) jdbcTemplate.query("SELECT * FROM USERS WHERE ID = ?", new Object[]{id}, new UserMapper())
                .stream().findAny().orElse(null);

        user.setFriendIds((Set<Long>) jdbcTemplate.query("SELECT USER_2 ID " +
                        "FROM FRIENDS " +
                        "WHERE USER_1=? AND STATUS = 'Подтвержденная'",
                new Object[]{id}, new SetMapper()).stream().collect(Collectors.toSet()));

        user.setFriendRequests((Set<Long>) jdbcTemplate.query("SELECT USER_2 ID " +
                        "FROM FRIENDS " +
                        "WHERE USER_1=? AND STATUS = 'Не подтвежденная'",
                new Object[]{id}, new SetMapper()).stream().collect(Collectors.toSet()));

        return user;
    }
}
