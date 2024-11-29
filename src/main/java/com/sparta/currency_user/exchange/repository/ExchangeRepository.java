package com.sparta.currency_user.exchange.repository;

import com.sparta.currency_user.exchange.dto.ExchangeSumDto;
import com.sparta.currency_user.exchange.entity.Exchange;
import com.sparta.currency_user.type.ExchangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    List<Exchange> findByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("update Exchange e set e.exchangeStatus = :exchangeStatus where e.user.id = :userId and e.currency.id = :currencyId")
    int updateExchangeStatus(@Param("exchangeStatus") ExchangeStatus exchangeStatus,
                             @Param("userId") Long userId,
                             @Param("currencyId") Long currencyId);


    @Query("SELECT new com.sparta.currency_user.exchange.dto.ExchangeSumDto(COUNT(e), SUM(e.amountInKrw)) " +
            "FROM Exchange e " +
            "WHERE e.user.id = :userId " +
            "GROUP BY e.user.id")
    ExchangeSumDto calculateExchangeSummary(@Param("userId") Long userId);

    List<Exchange> findByUserIdAndCurrencyId(Long userId, Long currencyId);

    @Transactional
    @Modifying
    @Query("update Exchange e set e.amountAfterExchange = :amountAfterExchange," +
            "e.exchangeStatus = :exchangeStatus where e.id = :exchangeId")
    int updateExchangeAmountAndStatus(@Param("exchangeId") Long exchangeId,
                                      @Param("amountAfterExchange")BigDecimal amountAfterExchange,
                                      @Param("exchangeStatus") ExchangeStatus exchangeStatus);
}
