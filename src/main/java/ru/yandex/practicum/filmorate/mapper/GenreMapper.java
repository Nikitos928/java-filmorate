package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.GenreMap;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapper implements RowMapper {
    @Override
    public GenreMap mapRow(ResultSet rs, int rowNum) throws SQLException {
        GenreMap genreMap = new GenreMap();

        genreMap.setFilmId(rs.getInt("film_id"));
        genreMap.setGenreId(rs.getInt("genre_id"));
        genreMap.setGenre(rs.getString("genre_name"));
        return genreMap;
    }
}
