package com.sparta.currency_user.currency.service;

import com.sparta.currency_user.currency.dto.CurrencyRequestDto;
import com.sparta.currency_user.currency.dto.CurrencyResponseDto;
import com.sparta.currency_user.currency.entity.Currency;
import com.sparta.currency_user.type.CurrencyName;

import java.math.BigDecimal;
import java.util.List;

public interface CurrencyService {
    CurrencyResponseDto findById(Long id);
    Currency findCurrencyById(Long id);
    List<CurrencyResponseDto> findAll();
    CurrencyResponseDto save(CurrencyRequestDto currencyRequestDto);
    //BigDecimal calculateExchangeAmount(BigDecimal amountInKrw, BigDecimal exchangeRate, boolean isWholeNumber);
}
