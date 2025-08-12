package dev.zux13.currency.exchange.util;

import lombok.experimental.UtilityClass;

import java.sql.SQLException;

@UtilityClass
public class SQLExceptionUtils {

    public boolean isUniqueConstraintViolation(Throwable ex) {
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof SQLException sqlEx) {
                int errorCode = sqlEx.getErrorCode();
                String sqlState = sqlEx.getSQLState();
                String message = sqlEx.getMessage();

                return errorCode == 19 ||
                        "23505".equals(sqlState) ||
                        (message != null && message.toLowerCase().contains("unique constraint"));
            }
            cause = cause.getCause();
        }
        return false;
    }

}
