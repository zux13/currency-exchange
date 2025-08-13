package dev.zux13.currency.exchange.dto;

import java.math.BigDecimal;

public record ExchangeResponseDto(CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
}
