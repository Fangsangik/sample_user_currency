package com.sparta.currency_user.type;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public enum CurrencyName {
    USD("$", false),
    EUR("€", false),
    KRW("₩", true),
    JPY("円", true),
    GBP("£", false);

    private final String symbol;
    private final boolean wholeNumber;

    CurrencyName(String symbol, boolean wholeNumber) {
        this.symbol = symbol;
        this.wholeNumber = wholeNumber;
    }


}
