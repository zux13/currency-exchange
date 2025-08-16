package dev.zux13.currency.exchange.listener;

import dev.zux13.currency.exchange.util.ConnectionManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    private static final String SCHEMA_SCRIPT_PATH = "/db/schema.sql";
    private static final String DATA_SCRIPT_PATH = "/db/data.sql";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String appRoot = sce.getServletContext().getRealPath("/");
        String dbUrl = "jdbc:sqlite:" + getDbFilePath(appRoot);

        ConnectionManager.setUrl(dbUrl);

        try (Connection connection = ConnectionManager.get()) {
            initializeSchema(connection);
            initializeData(connection);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private static String getDbFilePath(String appRoot) {
        if (appRoot == null) {
            throw new IllegalStateException("Failed to determine application root path. Is the application deployed as exploded WAR?");
        }

        File dbDir = new File(appRoot, "db");
        if (!dbDir.exists()) {
            if (!dbDir.mkdirs()) {
                throw new RuntimeException("Failed to create directory for database: " + dbDir.getAbsolutePath());
            }
        }

        if (!dbDir.isDirectory()) {
            throw new RuntimeException("Database path is not a directory: " + dbDir.getAbsolutePath());
        }

        return new File(dbDir, "currency_exchange.db").getAbsolutePath();
    }

    private void initializeSchema(Connection connection) throws SQLException, IOException {
        executeSqlScript(connection, SCHEMA_SCRIPT_PATH);
    }

    private void initializeData(Connection connection) throws SQLException, IOException {
        executeSqlScript(connection, DATA_SCRIPT_PATH);
    }

    private void executeSqlScript(Connection connection, String resourcePath) throws IOException, SQLException {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException(resourcePath + " not found");
            }
            String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String[] statements = sql.split(";");

            try (Statement statement = connection.createStatement()) {
                for (String stmt : statements) {
                    stmt = stmt.trim();
                    if (!stmt.isEmpty()) {
                        statement.execute(stmt);
                    }
                }
            }
        }
    }
}
