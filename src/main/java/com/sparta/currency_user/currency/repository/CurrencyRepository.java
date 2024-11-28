package com.sparta.currency_user.currency.repository;

import com.sparta.currency_user.currency.entity.Currency;
import com.sparta.currency_user.type.CurrencyName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCurrencyName(CurrencyName currencyName);
}
