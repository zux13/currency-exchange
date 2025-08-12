package dev.zux13.currency.exchange.exception;

public class DuplicateExchangeRateException extends RuntimeException {

    public DuplicateExchangeRateException(String baseCurrencyCode, String targetCurrencyCode) {
        super("Exchange rate for %s and %s already exists".formatted(baseCurrencyCode, targetCurrencyCode));
    }
}
