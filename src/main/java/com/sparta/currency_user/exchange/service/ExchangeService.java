package com.sparta.currency_user.exchange.service;

import com.sparta.currency_user.exchange.dto.ExchangeRequestDto;
import com.sparta.currency_user.exchange.dto.ExchangeResponseDto;
import com.sparta.currency_user.exchange.dto.ExchangeSumDto;
import com.sparta.currency_user.type.ExchangeStatus;

import java.util.List;

public interface ExchangeService {

    ExchangeResponseDto performExchange(ExchangeRequestDto exchangeRequestDto);
    ExchangeResponseDto getExchangeById(Long userId);
    List<ExchangeResponseDto> updateExchangeStatus(Long userId, Long currencyId, ExchangeStatus exchangeStatus);
    List<ExchangeResponseDto> getExchanges(Long userId);
    ExchangeSumDto getExchangeSumDto(Long userId);
}
