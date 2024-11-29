package com.sparta.currency_user.exchange.formatter;

import com.sparta.currency_user.currency.dto.CurrencyResponseDto;

import java.math.BigDecimal;

public interface ExchangeFormatter {
    String formatAmount(BigDecimal amount, CurrencyResponseDto responseDto);
}
