package dev.zux13.currency.exchange.dao;

import dev.zux13.currency.exchange.entity.ExchangeRate;
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
public class ExchangeRateDao implements Dao<Long, ExchangeRate> {

    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();

    private static final String FIND_ALL = "SELECT * FROM ExchangeRates";
    private static final String FIND_BY_ID = "SELECT * FROM ExchangeRates WHERE Id = ?";
    private static final String SAVE = "INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES(?, ?, ?)";
    private static final String UPDATE = "UPDATE ExchangeRates SET BaseCurrencyId = ?, TargetCurrencyId = ?, Rate = ? WHERE ID = ?";
    private static final String DELETE = "DELETE FROM ExchangeRates WHERE ID = ?";
    private static final String FIND_BY_CURRENCY_IDS = """
                                SELECT * FROM ExchangeRates
                                WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?
                                """;

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }

    @Override
    public List<ExchangeRate> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL)) {

            var resultSet = preparedStatement.executeQuery();
            List<ExchangeRate> result = new ArrayList<>();

            while (resultSet.next()) {
                result.add(buildExchangeRate(resultSet));
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRate> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID)) {

            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(buildExchangeRate(resultSet));
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExchangeRate save(ExchangeRate entity) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, entity.getBaseCurrencyId());
            preparedStatement.setLong(2, entity.getTargetCurrencyId());
            preparedStatement.setDouble(3, entity.getRate());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Saving exchange rate failed, no rows affected.");
            }

            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Saving exchange rate failed, no ID obtained.");
                }
            }

            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ExchangeRate entity) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE)) {

            preparedStatement.setLong(1, entity.getBaseCurrencyId());
            preparedStatement.setLong(2, entity.getTargetCurrencyId());
            preparedStatement.setDouble(3, entity.getRate());
            preparedStatement.setLong(4, entity.getId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating exchange rate failed, no rows affected.");
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

    public Optional<ExchangeRate> findByCurrencyIds(Long baseCurrencyId, Long targetCurrencyId) {
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(FIND_BY_CURRENCY_IDS)) {
            ps.setLong(1, baseCurrencyId);
            ps.setLong(2, targetCurrencyId);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(buildExchangeRate(rs));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return ExchangeRate.builder()
                .id(resultSet.getLong("ID"))
                .baseCurrencyId(resultSet.getLong("BaseCurrencyId"))
                .targetCurrencyId(resultSet.getLong("TargetCurrencyId"))
                .rate(resultSet.getDouble("Rate"))
                .build();
    }
}
