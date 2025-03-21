package org.example.expert.domain.user.repository;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<User> users) {
        String sql = "INSERT INTO users (email,password,nickname) values (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql,
                users,
                users.size(), (ps, user) -> {
                    ps.setString(1, user.getEmail());
                    ps.setString(2, user.getPassword());
                    ps.setString(3, user.getNickname());
                });
    }
}
