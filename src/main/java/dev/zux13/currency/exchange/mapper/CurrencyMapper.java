package dev.zux13.currency.exchange.mapper;

import dev.zux13.currency.exchange.dto.CurrencyDto;
import dev.zux13.currency.exchange.entity.Currency;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CurrencyMapper {

    public CurrencyDto toDto(Currency entity) {
        return CurrencyDto.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .sign(entity.getSign())
                .build();
    }

    public Currency toEntity(CurrencyDto dto) {
        return Currency.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .sign(dto.getSign())
                .name(dto.getName())
                .build();
    }
}
