package dev.zux13.currency.exchange.mapper;

import dev.zux13.currency.exchange.dto.CurrencyDto;
import dev.zux13.currency.exchange.entity.Currency;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CurrencyMapper {

    public CurrencyDto toDto(Currency entity) {
        return new CurrencyDto(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getSign()
        );
    }

    public Currency toEntity(CurrencyDto dto) {
        return Currency.builder()
                .id(dto.id())
                .code(dto.code())
                .sign(dto.sign())
                .name(dto.name())
                .build();
    }
}
