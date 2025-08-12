package dev.zux13.currency.exchange.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyDto {
    private Long id;
    private String name;
    private String code;
    private String sign;
}
