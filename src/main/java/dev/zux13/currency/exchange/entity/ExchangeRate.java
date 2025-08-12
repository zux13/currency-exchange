package dev.zux13.currency.exchange.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExchangeRate {

    @EqualsAndHashCode.Include
    private Long id;
    private Long baseCurrencyId;
    private Long targetCurrencyId;
    private double rate;
}
