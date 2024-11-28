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

    public String formatAmount(BigDecimal amount) {
        if (wholeNumber) {
            // 소수점 이하를 버리도록 설정
            amount = amount.setScale(0, RoundingMode.DOWN);
        } else {
            // 소수점 이하 2자리로 반올림 설정 (추가적으로 필요한 경우)
            amount = amount.setScale(2, RoundingMode.HALF_UP);
        }
        return amount.toPlainString() + " " + symbol;
    }
}
