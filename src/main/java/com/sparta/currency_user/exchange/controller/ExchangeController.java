package com.sparta.currency_user.exchange.controller;

import com.sparta.currency_user.exchange.dto.ExchangeRequestDto;
import com.sparta.currency_user.exchange.dto.ExchangeResponseDto;
import com.sparta.currency_user.exchange.dto.ExchangeSumDto;
import com.sparta.currency_user.exchange.service.ExchangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exchanges")
public class ExchangeController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ExchangeResponseDto> getExchanges(@PathVariable Long userId) {
        ExchangeResponseDto exchangeById = exchangeService.getExchangeById(userId);
        return ResponseEntity.ok(exchangeById);
    }

    @GetMapping("/{exchangeId}")
    public ResponseEntity<ExchangeResponseDto> getExchangeById(@PathVariable Long exchangeId) {
        ExchangeResponseDto exchangeById = exchangeService.getExchangeById(exchangeId);
        return ResponseEntity.ok(exchangeById);
    }

    @PostMapping
    public ResponseEntity<ExchangeResponseDto> performExchange(@RequestBody ExchangeRequestDto exchangeRequest) {
        ExchangeResponseDto exchangeById = exchangeService.performExchange(exchangeRequest);
        return ResponseEntity.ok(exchangeById);
    }

    @PutMapping("/update-status")
    public ResponseEntity<List<ExchangeResponseDto>> updateExchangeStatus(@RequestBody ExchangeRequestDto request) {
        List<ExchangeResponseDto> response = exchangeService.updateExchangeStatus(
                request.getUserId(), request.getCurrencyId(), request.getExchangeStatus());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sum/{userId}")
    public ResponseEntity<ExchangeSumDto> getExchangeSum(@PathVariable Long userId) {
        ExchangeSumDto exchangeSumDto = exchangeService.getExchangeSumDto(userId);
        return ResponseEntity.ok(exchangeSumDto);
    }
}
