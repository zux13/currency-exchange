package dev.zux13.currency.exchange.dao;

import dev.zux13.currency.exchange.dao.mapper.ExchangeRateRowMapper;
import dev.zux13.currency.exchange.dao.template.JdbcTemplate;
import dev.zux13.currency.exchange.entity.ExchangeRate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateDao {

    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();
    private static final String FIND_ALL_SQL = "SELECT * FROM ExchangeRates";
    private static final String SAVE_SQL = "INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) VALUES(?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE ExchangeRates SET BaseCurrencyId = ?, TargetCurrencyId = ?, Rate = ? WHERE ID = ?";
    private static final String DELETE_SQL = "DELETE FROM ExchangeRates WHERE ID = ?";
    private static final String FIND_BY_CURRENCY_IDS_SQL = """
                                SELECT * FROM ExchangeRates
                                WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?
                                """;

    private final JdbcTemplate jdbcTemplate = JdbcTemplate.getInstance();
    private final ExchangeRateRowMapper exchangeRateRowMapper = new ExchangeRateRowMapper();

    public List<ExchangeRate> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, exchangeRateRowMapper);
    }

    public Optional<ExchangeRate> findByCurrencyIds(Long baseCurrencyId, Long targetCurrencyId) {
        return jdbcTemplate.queryForObject(FIND_BY_CURRENCY_IDS_SQL,
                exchangeRateRowMapper,
                baseCurrencyId,
                targetCurrencyId
        );
    }

    public ExchangeRate save(ExchangeRate entity) {
        return jdbcTemplate.updateAndReturnGeneratedKey(
                SAVE_SQL,
                rs -> {
                    entity.setId(rs.getLong(1));
                    return entity;
                },
                entity.getBaseCurrencyId(),
                entity.getTargetCurrencyId(),
                entity.getRate()
        );
    }

    public void update(ExchangeRate entity) {
        jdbcTemplate.update(UPDATE_SQL,
                entity.getBaseCurrencyId(),
                entity.getTargetCurrencyId(),
                entity.getRate(),
                entity.getId()
        );
    }

    public boolean delete(Long id) {
        return jdbcTemplate.update(DELETE_SQL, id) > 0;
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }
}
