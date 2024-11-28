package com.sparta.currency_user.exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
public class ExchangeSumDto {
    private Long count;
    private BigDecimal totalAmountIntKrw;

    public ExchangeSumDto(Long count, BigDecimal totalAmountIntKrw) {
        this.count = count;
        this.totalAmountIntKrw = totalAmountIntKrw;
    }
}
