package dev.zux13.currency.exchange.dao.template;

import dev.zux13.currency.exchange.dao.mapper.RowMapper;
import dev.zux13.currency.exchange.util.ConnectionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JdbcTemplate {

    private final static JdbcTemplate INSTANCE = new JdbcTemplate();

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... params) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, params);
            ResultSet rs = statement.executeQuery();
            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(rowMapper.map(rs));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        }
    }

    public <T> Optional<T> queryForObject(String sql, RowMapper<T> rowMapper, Object... params) {
        List<T> results = query(sql, rowMapper, params);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    public int update(String sql, Object... params) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, params);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database update failed", e);
        }
    }

    public <T> T updateAndReturnGeneratedKey(String sql, RowMapper<T> keyMapper, Object... params) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(statement, params);
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return keyMapper.map(generatedKeys);
            } else {
                throw new SQLException("Creating record failed, no ID obtained.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database update with generated key failed", e);
        }
    }

    public static JdbcTemplate getInstance() {
        return INSTANCE;
    }

    private void setParameters(PreparedStatement statement, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }
}
