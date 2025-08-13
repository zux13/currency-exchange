package dev.zux13.currency.exchange.dao.mapper;

import dev.zux13.currency.exchange.entity.Currency;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyRowMapper implements RowMapper<Currency> {
    @Override
    public Currency map(ResultSet rs) throws SQLException {
        return Currency.builder()
                .id(rs.getLong("ID"))
                .name(rs.getString("FullName"))
                .code(rs.getString("Code"))
                .sign(rs.getString("Sign"))
                .build();
    }
}
