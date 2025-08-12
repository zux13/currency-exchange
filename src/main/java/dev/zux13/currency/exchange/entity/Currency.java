package dev.zux13.currency.exchange.entity;

import lombok.*;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Currency {

    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private String code;
    private String sign;
}
