package com.sparta.currency_user.exchange.entity;

import com.sparta.currency_user.currency.common.BaseEntity;
import com.sparta.currency_user.currency.entity.Currency;
import com.sparta.currency_user.type.ExchangeStatus;
import com.sparta.currency_user.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Exchange extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 고객 (User 테이블과의 관계)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency; // 환전 대상 통화 (Currency 테이블과의 관계)

    private int exchangeCount;

    @Enumerated(EnumType.STRING)
    private ExchangeStatus exchangeStatus;

    private BigDecimal amountInKrw; // 환전 전 금액 (원화 기준)
    private BigDecimal amountAfterExchange; // 환전 후 금액

    @Builder
    public Exchange(User user, Currency currency, ExchangeStatus exchangeStatus, BigDecimal amountInKrw, BigDecimal amountAfterExchange) {
        this.user = user;
        this.currency = currency;
        this.exchangeStatus = exchangeStatus;
        this.amountInKrw = amountInKrw;
        this.amountAfterExchange = amountAfterExchange;
    }

    public Exchange() {

    }
}
