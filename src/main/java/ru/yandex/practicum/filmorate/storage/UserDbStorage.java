package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.SetMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Qualifier("InDbUserStorage")
public class UserDbStorage implements UserStorage {

    private Long id = 1L;

    private final JdbcTemplate jdbcTemplate;


    @Override
    public User addUser(User user) {
        user.setId(id);
        jdbcTemplate.batchUpdate("INSERT INTO USERS VALUES (?,?,?,?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, user.getId().intValue());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getLogin());
                preparedStatement.setString(4, user.getName());
                preparedStatement.setDate(5, Date.valueOf(user.getBirthday()));
            }
            @Override
            public int getBatchSize() {
                return 1;
            }
        });
        id++;
        return user;
    }

    @Override
    public User updateUser(User user) {

        jdbcTemplate.update("DELETE FROM FRIENDS WHERE USER_1 = ? AND STATUS = true", user.getId());
        jdbcTemplate.update("DELETE FROM FRIENDS WHERE USER_1 = ? AND STATUS = false", user.getId());

        for (Long friend : user.getFriendIds()) {
            if (getUser(friend).getFriendIds().contains(user.getId())) {
                jdbcTemplate.update("DELETE FROM FRIENDS" +
                        " WHERE USER_1 = ? AND USER_2 = ? AND STATUS = ? ", friend, user.getId(), false);
                jdbcTemplate.update("INSERT INTO FRIENDS VALUES (?,?,?)", user.getId(), friend, true);
            } else {
                jdbcTemplate.update("INSERT INTO FRIENDS VALUES (?,?,?)", user.getId(), friend, false);
            }
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
        List<User> users = new ArrayList<>(jdbcTemplate.query("SELECT * FROM USERS", new UserMapper()));

        Map<Long, HashSet<Long>> friends = new HashMap<>();

        for (Like like : jdbcTemplate.query("SELECT USER_1, USER_2 " +
                        "FROM FRIENDS " +
                        "WHERE STATUS = true",
                (rs, rowNum) -> new Like(rs.getLong(1), rs.getLong(2)))) {
            HashSet<Long> friendsSet = new HashSet<>();
            friendsSet.add(like.getLike2());
            friends.put(like.getLike1(), friendsSet);
        }

        for (Like like : jdbcTemplate.query("SELECT USER_2, USER_1 " +
                        "FROM FRIENDS " +
                        "WHERE STATUS = true",
                (rs, rowNum) -> new Like(rs.getLong(1), rs.getLong(2)))) {

            if (!friends.containsKey(like.getLike1())) {
                HashSet<Long> friendsSet = new HashSet<>();
                friendsSet.add(like.getLike2());
                friends.put(like.getLike1(), friendsSet);
            } else {
                friends.get(like.getLike1()).add(like.getLike2());
            }
        }

        for (Like like : jdbcTemplate.query("SELECT USER_2, USER_1 " +
                        "FROM FRIENDS " +
                        "WHERE STATUS = false",
                (rs, rowNum) -> new Like(rs.getLong(1), rs.getLong(2)))) {
            if (!friends.containsKey(like.getLike2())) {
                HashSet<Long> friendsSet = new HashSet<>();
                friendsSet.add(like.getLike1());
                friends.put(like.getLike2(), friendsSet);
            } else {
                friends.get(like.getLike2()).add(like.getLike1());
            }
        }

        for (User user : users) {
            if (friends.containsKey(user.getId())) {
                user.setFriendIds(friends.get(user.getId()));
            }
        }
        return users;
    }

    @Override
    public User getUser(Long id) {

        User user = (User) jdbcTemplate.query("SELECT * FROM USERS WHERE ID = ?", new UserMapper(), id)
                .stream().findAny().orElse(null);

        user.getFriendIds().addAll((Set<Long>) jdbcTemplate.query("SELECT USER_2 ID " +
                        "FROM FRIENDS " +
                        "WHERE USER_1=? AND STATUS = true",
                new SetMapper(), id).stream().collect(Collectors.toSet()));

        user.getFriendIds().addAll((Set<Long>) jdbcTemplate.query("SELECT USER_1 ID " +
                        "FROM FRIENDS " +
                        "WHERE USER_2=? AND STATUS = true",
                new SetMapper(), id).stream().collect(Collectors.toSet()));

        user.getFriendIds().addAll((Set<Long>) jdbcTemplate.query("SELECT USER_2 ID " +
                        "FROM FRIENDS " +
                        "WHERE USER_1 = ? AND STATUS = false",
                new SetMapper(), id).stream().collect(Collectors.toSet()));

        return user;
    }

    @Override
    public List<User> getFriends(Long id) {

        Set<Long> friends = getUser(id).getFriendIds();

        String friendsIdToString = "SELECT * FROM USERS WHERE ID IN ( " +
                friends.toString().replace("[", "").replace("]", "") + " ) ";

        return new ArrayList<>(jdbcTemplate.query(friendsIdToString, new UserMapper()));
    }

    @Override
    public List<User> mutualFriends(Long userId1, Long userId2) {

        String friendsIdToString = "SELECT * FROM USERS WHERE ID IN ( " +
                getUser(userId1).getFriendIds()
                        .stream()
                        .filter(getUser(userId2).getFriendIds()::contains)
                        .collect(Collectors.toList()).toString().replace("[", "").replace("]", "") + " ) ";

        return new ArrayList<>(jdbcTemplate.query(friendsIdToString, new UserMapper()));
    }

}