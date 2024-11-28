package com.sparta.currency_user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CurrencyUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurrencyUserApplication.class, args);
    }

}
