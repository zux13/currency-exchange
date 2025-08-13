package dev.zux13.currency.exchange.exception;

public class DuplicateExchangeRateException extends RuntimeException {

    public DuplicateExchangeRateException(String message) {
        super(message);
    }
}
