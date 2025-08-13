package dev.zux13.currency.exchange.util;

import lombok.experimental.UtilityClass;

import java.sql.SQLException;

@UtilityClass
public class SQLExceptionUtils {

    private static final String UNIQUE_VIOLATION_MESSAGE = "UNIQUE constraint failed";
    private static final String UNIQUE_VIOLATION_SQL_STATE = "23505";

    public boolean isUniqueConstraintViolation(Throwable ex) {
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof SQLException sqlEx) {
                String message = sqlEx.getMessage();
                return UNIQUE_VIOLATION_SQL_STATE.equals(sqlEx.getSQLState())
                        || (message != null && message.contains(UNIQUE_VIOLATION_MESSAGE));
            }
            cause = cause.getCause();
        }
        return false;
    }

}
