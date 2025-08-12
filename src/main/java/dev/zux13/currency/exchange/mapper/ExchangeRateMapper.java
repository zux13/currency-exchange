package dev.zux13.currency.exchange.mapper;

import dev.zux13.currency.exchange.dto.CurrencyDto;
import dev.zux13.currency.exchange.dto.ExchangeRateResponseDto;
import dev.zux13.currency.exchange.dto.ExchangeRateSaveDto;
import dev.zux13.currency.exchange.entity.ExchangeRate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExchangeRateMapper {

    public ExchangeRateResponseDto toDto(ExchangeRate entity, CurrencyDto base, CurrencyDto target) {
        return ExchangeRateResponseDto.builder()
                .id(entity.getId())
                .baseCurrency(base)
                .targetCurrency(target)
                .rate(entity.getRate())
                .build();
    }

    public ExchangeRate toEntity(ExchangeRateSaveDto dto, Long baseCurrencyId, Long targetCurrencyId) {
        return ExchangeRate.builder()
                .baseCurrencyId(baseCurrencyId)
                .targetCurrencyId(targetCurrencyId)
                .rate(dto.getRate())
                .build();
    }

}
