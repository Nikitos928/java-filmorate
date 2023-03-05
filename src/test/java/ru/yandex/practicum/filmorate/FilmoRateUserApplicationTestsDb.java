package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateUserApplicationTestsDb {
    private final UserDbStorage userStorage;


    @Test
    public void UserDbStorageTest() {

        User user1 = User.builder()
                .birthday(LocalDate.of(2000, 12, 12))
                .email("rrr@mail.ru")
                .login("Login")
                .name("Name")
                .friendIds(new HashSet<>())
                .build();

        userStorage.addUser(user1);

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUser(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Name")
                );

        User user2 = User.builder()
                .birthday(LocalDate.of(2000, 12, 12))
                .email("rrr@mail.ru")
                .login("Login1")
                .name("Name1")
                .friendIds(new HashSet<>())
                .build();

        userStorage.addUser(user2);

        Optional<User> userOptional1 = Optional.ofNullable(userStorage.getUser(2L));

        assertThat(userOptional1)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2L)
                                .hasFieldOrPropertyWithValue("name", "Name1")
                );

        Optional<List<User>> userOptional2 = Optional.ofNullable(userStorage.getUsers());

        assertThat(userOptional2).isPresent().hasValueSatisfying(AssertionsForClassTypes::assertThat)
                .hasValue(Arrays.asList(user1, user2));

        user1.setName("НовоеИмя");
        user2.setName("НовоеИмя");
        user1.setLogin("НовыйЛогин");
        user2.setLogin("НовыйЛогин");
        userStorage.updateUser(user1);
        userStorage.updateUser(user2);
        Optional<List<User>> userOptional3 = Optional.ofNullable(userStorage.getUsers());

        assertThat(userOptional3).isPresent().hasValueSatisfying(AssertionsForClassTypes::assertThat)
                .hasValue(Arrays.asList(user1, user2));

        Set<Long> fr = new HashSet<>();
        fr.add(1L);
        fr.add(2L);
        User user3 = User.builder()
                .birthday(LocalDate.of(2000, 12, 12))
                .email("rrr@mail.ru")
                .login("Login")
                .name("Name")
                .friendIds(new HashSet<>())
                .build();

        userStorage.addUser(user3);
        user3.setFriendIds(fr);
        userStorage.updateUser(user3);

        Optional<List<User>> userOptional4 = Optional.ofNullable(userStorage.getFriends(user3.getId()));

        assertThat(userOptional4).isPresent().hasValueSatisfying(AssertionsForClassTypes::assertThat)
                .hasValue(Arrays.asList(user1, user2));

        user2.getFriendIds().add(1L);
        userStorage.updateUser(user2);

        Optional<List<User>> userOptional5 = Optional.ofNullable(userStorage.mutualFriends(2L, 3L));

        assertThat(userOptional5).isPresent().hasValueSatisfying(AssertionsForClassTypes::assertThat)
                .hasValue(Arrays.asList(user1));

    }

}
