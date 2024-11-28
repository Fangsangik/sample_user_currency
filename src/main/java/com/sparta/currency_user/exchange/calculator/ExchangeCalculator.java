package com.sparta.currency_user.exchange.calculator;

import com.sparta.currency_user.exception.CustomError;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.sparta.currency_user.exception.type.ErrorType.INVALID_CURRENCY_EXCEPTION;

/**
 * 추상클래스를 활용해서 각 상황에 따라 소수점 처리를 달리 하게 했다.
 */
public abstract class ExchangeCalculator {

    public BigDecimal calculateExchangeAmount(BigDecimal amountInKrw, BigDecimal exchangeRate, boolean isWholeNumber) {
        if (exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new CustomError(INVALID_CURRENCY_EXCEPTION);
        }

        int scale = getScale(isWholeNumber);
        RoundingMode roundingMode = getRoundingMode(isWholeNumber);

        return amountInKrw.divide(exchangeRate, scale, roundingMode);
    }

    protected abstract int getScale(boolean isWholeNumber);

    protected abstract RoundingMode getRoundingMode(boolean isWholeNumber);
}
