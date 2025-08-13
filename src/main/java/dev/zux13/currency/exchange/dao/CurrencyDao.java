package dev.zux13.currency.exchange.dao;

import dev.zux13.currency.exchange.dao.mapper.CurrencyRowMapper;
import dev.zux13.currency.exchange.dao.template.JdbcTemplate;
import dev.zux13.currency.exchange.entity.Currency;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyDao {

    private static final CurrencyDao INSTANCE = new CurrencyDao();
    private static final String FIND_ALL_SQL = "SELECT ID, Code, FullName, Sign FROM Currencies";
    private static final String FIND_BY_ID_SQL = "SELECT ID, Code, FullName, Sign FROM Currencies WHERE ID = ?";
    private static final String FIND_BY_CODE_SQL = "SELECT ID, Code, FullName, Sign FROM Currencies WHERE Code = ?";
    private static final String SAVE_SQL = "INSERT INTO Currencies(Code, FullName, Sign) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Currencies SET Code = ?, FullName = ?, Sign = ? WHERE ID = ?";
    private static final String DELETE_SQL = "DELETE FROM Currencies WHERE ID = ?";

    private final JdbcTemplate jdbcTemplate = JdbcTemplate.getInstance();
    private final CurrencyRowMapper currencyRowMapper = new CurrencyRowMapper();

    public List<Currency> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, currencyRowMapper);
    }

    public Optional<Currency> findById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, currencyRowMapper, id);
    }

    public Optional<Currency> findByCode(String code) {
        return jdbcTemplate.queryForObject(FIND_BY_CODE_SQL, currencyRowMapper, code);
    }

    public Currency save(Currency entity) {
        return jdbcTemplate.updateAndReturnGeneratedKey(
                SAVE_SQL,
                rs -> {
                    entity.setId(rs.getLong(1));
                    return entity;
                },
                entity.getCode(),
                entity.getName(),
                entity.getSign()
        );
    }

    public void update(Currency entity) {
        jdbcTemplate.update(UPDATE_SQL,
                entity.getCode(),
                entity.getName(),
                entity.getSign(),
                entity.getId()
        );
    }

    public boolean delete(Long id) {
        return jdbcTemplate.update(DELETE_SQL, id) > 0;
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }
}
