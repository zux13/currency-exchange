package dev.zux13.currency.exchange.validation;

import dev.zux13.currency.exchange.exception.ValidationException;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class Validator {

    public static final int CURRENCY_CODE_LENGTH = 3;
    private static final int CURRENCY_PAIR_LENGTH = 6;
    private static final int MAX_CURRENCY_NAME_LENGTH = 40;
    private static final int PATH_PREFIX_LENGTH = 1;
    private static final String CURRENCY_CODE_REGEX = "^[A-Z]{" + CURRENCY_CODE_LENGTH + "}$";
    private static final String CURRENCY_PAIR_REGEX = "^[A-Z]{" + CURRENCY_PAIR_LENGTH + "}$";

    public String validateAndExtractPath(String pathInfo) {
        if (pathInfo == null || "/".equals(pathInfo)) {
            throw new ValidationException("A parameter is required in the path");
        }
        return pathInfo.substring(PATH_PREFIX_LENGTH).toUpperCase();
    }

    public void validateCurrencyCode(String code) {
        if (code == null || !code.matches(CURRENCY_CODE_REGEX)) {
            throw new ValidationException("Currency code must be " + CURRENCY_CODE_LENGTH + " uppercase letters");
        }
    }

    public void validateCurrencyPair(String pair) {
        if (pair == null || !pair.matches(CURRENCY_PAIR_REGEX)) {
            throw new ValidationException("Currency pair must be " + CURRENCY_PAIR_LENGTH + " uppercase letters (e.g., USDRUB)");
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

    public void validateCurrencyName(String name) {
        validateFormField(name, "name");
        if (name.length() > MAX_CURRENCY_NAME_LENGTH) {
            throw new ValidationException("Currency name must be no more than " + MAX_CURRENCY_NAME_LENGTH + " characters");
        }
    }

    public void validateDifferentCurrencies(String baseCode, String targetCode) {
        if (baseCode.equals(targetCode)) {
            throw new ValidationException("Base and target currencies must be different");
        }
    }
}
