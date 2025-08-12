package dev.zux13.currency.exchange.dao;

import dev.zux13.currency.exchange.entity.Currency;
import dev.zux13.currency.exchange.util.ConnectionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyDao implements Dao<Long, Currency> {

    private static final CurrencyDao INSTANCE = new CurrencyDao();
    private static final String FIND_ALL = "SELECT * FROM Currencies";
    private static final String FIND_BY_ID = "SELECT * FROM Currencies WHERE id=?";
    private static final String FIND_BY_CODE = "SELECT * FROM Currencies WHERE Code=?";
    private static final String SAVE = "INSERT INTO Currencies(Code, FullName, Sign) VALUES(?, ?, ?)";
    private static final String UPDATE = "UPDATE Currencies SET Code = ?, FullName = ?, Sign = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM Currencies WHERE id = ?";

    @Override
    public List<Currency> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Currency> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(buildCurrency(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Currency> findByCode(String code) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_CODE)) {
            preparedStatement.setString(1, code);
            var resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(buildCurrency(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Currency save(Currency entity) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(
                     SAVE, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, entity.getCode());
            preparedStatement.setString(2, entity.getName());
            preparedStatement.setString(3, entity.getSign());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating currency failed, no rows affected.");
            }

            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating currency failed, no ID obtained.");
                }
            }

            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Currency entity) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE)) {

            preparedStatement.setString(1, entity.getCode());
            preparedStatement.setString(2, entity.getName());
            preparedStatement.setString(3, entity.getSign());
            preparedStatement.setLong(4, entity.getId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating currency failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE)) {

            preparedStatement.setLong(1, id);
            int affectedRows = preparedStatement.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return Currency.builder()
                .id(resultSet.getLong("id"))
                .code(resultSet.getString("code"))
                .name(resultSet.getString("fullName"))
                .sign(resultSet.getString("sign"))
                .build();
    }
}
