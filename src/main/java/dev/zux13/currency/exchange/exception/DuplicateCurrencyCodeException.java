package dev.zux13.currency.exchange.exception;

public class DuplicateCurrencyCodeException extends RuntimeException {

    public DuplicateCurrencyCodeException(String message) {
        super(message);
    }
}
