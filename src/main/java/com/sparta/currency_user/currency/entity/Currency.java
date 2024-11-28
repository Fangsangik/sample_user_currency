package com.sparta.currency_user.currency.entity;

import com.sparta.currency_user.exchange.entity.Exchange;
import com.sparta.currency_user.type.CurrencyName;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CurrencyName currencyName;

    private BigDecimal exchangeRate;
    private String symbol;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL)
    private List<Exchange> exchanges = new ArrayList<>();

    @Builder
    public Currency(CurrencyName currencyName, BigDecimal exchangeRate, String symbol) {
        this.currencyName = currencyName;
        this.exchangeRate = exchangeRate;
        this.symbol = symbol;
    }

    public Currency(Long currencyId) {}

    public Currency() {

    }
}
