package dev.zux13.currency.exchange.dto;

import java.math.BigDecimal;

public record ExchangeRateSaveDto(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
}
