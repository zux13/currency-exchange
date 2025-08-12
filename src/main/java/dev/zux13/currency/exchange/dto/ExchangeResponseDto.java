package dev.zux13.currency.exchange.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ExchangeResponseDto {
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private double rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}
