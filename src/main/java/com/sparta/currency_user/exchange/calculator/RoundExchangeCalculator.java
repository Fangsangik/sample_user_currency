package com.sparta.currency_user.exchange.calculator;

import org.springframework.stereotype.Component;

import java.math.RoundingMode;

@Component
public class RoundExchangeCalculator extends ExchangeCalculator {
    @Override
    protected int getScale(boolean isWholeNumber) {
        return isWholeNumber ? 0 : 2;
    }

    @Override
    protected RoundingMode getRoundingMode(boolean isWholeNumber) {
        return isWholeNumber ? RoundingMode.DOWN : RoundingMode.HALF_DOWN;
    }
}
