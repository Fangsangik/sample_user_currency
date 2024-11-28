package com.sparta.currency_user.currency.dto;

import com.sparta.currency_user.currency.entity.Currency;
import com.sparta.currency_user.type.CurrencyName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CurrencyRequestDto {
    @NotNull(message = "통화 이름은 필수 입력 값 입니다.")
    private CurrencyName currencyName;

    @NotNull(message = "환율은 필수 입력 값입니다.")
    @Positive(message = "환율은 0보다 커야 합니다.")
    private BigDecimal exchangeRate;
    private String symbol;

    public static Currency toEntity(CurrencyRequestDto currencyRequestDto) {
        return Currency.builder()
                .exchangeRate(currencyRequestDto.getExchangeRate())
                .currencyName(currencyRequestDto.getCurrencyName())
                .symbol(currencyRequestDto.getSymbol())
                .build();
    }

    // CurrencyName과 symbol 필드 검증
    public boolean checkSymbol() {
        return this.getCurrencyName().getSymbol().equals(this.getSymbol());
    }
}
