package dev.zux13.currency.exchange.exception;

public class ExchangeRateNotFoundException extends RuntimeException {

    public ExchangeRateNotFoundException(Long baseCurrencyId, Long targetCurrencyId) {
        super("Exchange rate for baseCurrencyId: %d and targetCurrencyId: %d not found"
                .formatted(baseCurrencyId, targetCurrencyId));
    }
}
