package dev.zux13.currency.exchange.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeRateResponseDto {
    private Long id;
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private double rate;
}
