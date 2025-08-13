package dev.zux13.currency.exchange.dao.mapper;

import dev.zux13.currency.exchange.entity.ExchangeRate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeRateRowMapper implements RowMapper<ExchangeRate> {
    @Override
    public ExchangeRate map(ResultSet rs) throws SQLException {
        return ExchangeRate.builder()
                .id(rs.getLong("ID"))
                .baseCurrencyId(rs.getLong("BaseCurrencyId"))
                .targetCurrencyId(rs.getLong("TargetCurrencyId"))
                .rate(rs.getBigDecimal("Rate"))
                .build();
    }
}
