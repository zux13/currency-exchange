package dev.zux13.currency.exchange.validation;

import dev.zux13.currency.exchange.exception.ValidationException;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class Validator {

    public String validateAndExtractPath(String pathInfo) {
        if (pathInfo == null || "/".equals(pathInfo)) {
            throw new ValidationException("A parameter is required in the path");
        }
        return pathInfo.substring(1).toUpperCase();
    }

    public void validateCurrencyCode(String code) {
        if (code == null || !code.matches("^[A-Z]{3}$")) {
            throw new ValidationException("Currency code must be 3 uppercase letters");
        }
    }

    public void validateCurrencyPair(String pair) {
        if (pair == null || !pair.matches("^[A-Z]{6}$")) {
            throw new ValidationException("Currency pair must be 6 uppercase letters (e.g., USDRUB)");
        }
    }

    public BigDecimal validateRate(String rateStr) {
        if (rateStr == null || rateStr.isBlank()) {
            throw new ValidationException("Rate is required");
        }
        try {
            BigDecimal rate = new BigDecimal(rateStr);
            if (rate.signum() <= 0) {
                throw new ValidationException("Rate must be a positive number");
            }
            return rate;
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid rate format");
        }
    }

    public BigDecimal validateAmount(String amountStr) {
        if (amountStr == null || amountStr.isBlank()) {
            throw new ValidationException("Amount is required");
        }
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.signum() <= 0) {
                throw new ValidationException("Amount must be a positive number");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid amount format");
        }
    }

    public void validateFormField(String field, String fieldName) {
        if (field == null || field.isBlank()) {
            throw new ValidationException("Field '%s' is required".formatted(fieldName));
        }
    }
}
