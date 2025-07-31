package dev.zux13.currency.exchange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    private static final String DB_URL = "jdbc:sqlite:currency_exchange.db";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            initializeSchema(connection);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initializeSchema(Connection connection) throws SQLException, IOException {
        try (InputStream inputStream = Main.class.getResourceAsStream("/db/schema.sql")) {
            if (inputStream == null) {
                throw new FileNotFoundException("schema.sql not found");
            }

            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sql);
            }
        }
    }
}
