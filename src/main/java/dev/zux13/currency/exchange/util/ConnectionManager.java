package dev.zux13.currency.exchange.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.DriverManager;

@UtilityClass
public class ConnectionManager {

    private static String DB_URL;

    static {
        loadDriver();
    }

    @SneakyThrows
    private void loadDriver() {
        Class.forName("org.sqlite.JDBC");
    }

    @SneakyThrows
    public Connection get() {
        if (DB_URL == null) {
            throw new IllegalStateException("Database URL not set");
        }
        return DriverManager.getConnection(DB_URL);
    }

    public static void setUrl(String url) {
        DB_URL = url;
    }
}
