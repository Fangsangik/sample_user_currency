package com.sparta.currency_user.exchange.dto;

import com.sparta.currency_user.exchange.entity.Exchange;
import com.sparta.currency_user.type.CurrencyName;
import com.sparta.currency_user.type.ExchangeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeResponseDto {
    private String userName;
    private CurrencyName currencyName;
    private BigDecimal amountInKrw;
    private BigDecimal amountAfterExchange;
    private ExchangeStatus exchangeStatus;
    private String formattedAmount;


    public static ExchangeResponseDto fromEntity(Exchange exchange,  String formattedAmount) {
        return ExchangeResponseDto.builder()
                .userName(exchange.getUser().getName())
                .currencyName(exchange.getCurrency().getCurrencyName())
                .amountInKrw(exchange.getAmountInKrw())
                .amountAfterExchange(exchange.getAmountAfterExchange())
                .exchangeStatus(exchange.getExchangeStatus())
                .formattedAmount(formattedAmount)
                .build();
    }

}
