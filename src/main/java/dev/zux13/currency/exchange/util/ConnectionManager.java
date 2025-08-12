package dev.zux13.currency.exchange.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.DriverManager;

@UtilityClass
public class ConnectionManager {

    private final static String DB_URL = "jdbc:sqlite:%s/currency_exchange.db"
            .formatted(System.getProperty("user.dir"));

    static {
        loadDriver();
    }

    @SneakyThrows
    private void loadDriver() {
        Class.forName("org.sqlite.JDBC");
    }

    @SneakyThrows
    public Connection get() {
        return DriverManager.getConnection(DB_URL);
    }
}
