package com.sparta.currency_user.exception.config;

import com.sparta.currency_user.exchange.calculator.ExchangeCalculator;
import com.sparta.currency_user.exchange.calculator.RoundExchangeCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CalculatorConfig {

    /**
     * 확정성을 고려한다면 Bean으로 등록해서 관리해야 하지 않을까 라는 생각이 든다.
     * 여러곳에서 DI를 적용한 다면 Bean으로 등록하는 것이 맞지만,
     * 상태를 유지 할 필요가 없고, 인스턴스간 공유 할 필요가 없다면, Spring으로 관리 하지 않아도 된다.
     * @return
     */
    @Bean
    public ExchangeCalculator calculator() {
        return new RoundExchangeCalculator();
    }
}
