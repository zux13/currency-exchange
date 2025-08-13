package dev.zux13.currency.exchange.dto;

import java.math.BigDecimal;

public record ExchangeRateResponseDto(Long id, CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate) {
}
