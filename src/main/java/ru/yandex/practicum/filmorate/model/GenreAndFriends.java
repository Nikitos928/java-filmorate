package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreAndFriends {
    private Set<Genre> setGenresInMap = new HashSet<>();
    private Set<Long> setLikesInMap = new HashSet<>();
}
