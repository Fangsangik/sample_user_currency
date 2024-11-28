package com.sparta.currency_user.currency.validate;

import com.sparta.currency_user.currency.entity.Currency;
import com.sparta.currency_user.currency.repository.CurrencyRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
/**
 * Spring이 구동될때 싫행되어 DB의 기존 데이터가 유효한지 검사
 * 애초에 0보다 작은 값을 입력을 하면 잘못 입력된 값입니다를 적용한다면,
 * CurrencyValidator라는 PostConstruct가 필요 할까?
 * DB에 값이 처음부터 잘못 들어가는 걸 막는다면 이 기능은 필요 없을 듯 하다.
 */
public class CurrencyValidator {

    private final CurrencyRepository currencyRepository;

    public CurrencyValidator(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @PostConstruct
    public void validateCurrency() {
      log.info("통화 유효성 검사 시작");

        List<Currency> currencies = currencyRepository.findAll();

        for (Currency currency : currencies) {
            BigDecimal exchangeRate = currency.getExchangeRate();
            if (exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("유효하지 않는 환률 - 통화 이름 : {}, 환률 : {}", currency.getCurrencyName(), currency.getExchangeRate());
            }

            log.info("통화 유효성 검사 종료");
        }
    }
}
