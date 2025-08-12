package dev.zux13.currency.exchange.exception;

public class CurrencyNotFoundException extends RuntimeException {

    public CurrencyNotFoundException(String code) {
        super("Currency with code %s not found".formatted(code));
    }

    public CurrencyNotFoundException(Long id) {
        super("Currency with id %d not found".formatted(id));
    }
}
