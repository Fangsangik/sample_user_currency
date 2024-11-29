package com.sparta.currency_user.exchange.formatter;

import com.sparta.currency_user.currency.dto.CurrencyResponseDto;
import com.sparta.currency_user.type.CurrencyName;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeFormatterImpl implements ExchangeFormatter {
    @Override
    public String formatAmount(BigDecimal amount, CurrencyResponseDto responseDto) {
        CurrencyName currencyName = CurrencyName.valueOf(responseDto.getCurrencyName().toString());
        if (currencyName.isWholeNumber()) {
            amount = amount.setScale(0, RoundingMode.DOWN);
        } else {
            amount = amount.setScale(2, RoundingMode.HALF_UP);
        }
        return amount.toPlainString() + " " + currencyName.getSymbol();
    }
}
