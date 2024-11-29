package com.sparta.currency_user.exception.config;

import com.sparta.currency_user.exchange.formatter.ExchangeFormatter;
import com.sparta.currency_user.exchange.formatter.ExchangeFormatterImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FormatterConfig {

    @Bean
    public ExchangeFormatter exchangeFormatter() {
        return new ExchangeFormatterImpl();
    }
}
