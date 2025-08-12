package dev.zux13.currency.exchange.exception;

public class DuplicateCurrencyCodeException extends RuntimeException {

    public DuplicateCurrencyCodeException(String code) {
        super("Currency with code %s already exists".formatted(code));
    }
}
