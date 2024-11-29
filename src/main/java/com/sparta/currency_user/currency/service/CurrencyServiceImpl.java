package com.sparta.currency_user.currency.service;

import com.sparta.currency_user.currency.dto.CurrencyRequestDto;
import com.sparta.currency_user.currency.dto.CurrencyResponseDto;
import com.sparta.currency_user.currency.entity.Currency;
import com.sparta.currency_user.currency.repository.CurrencyRepository;
import com.sparta.currency_user.currency.validate.CurrencyValidator;
import com.sparta.currency_user.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sparta.currency_user.exception.type.ErrorType.*;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyValidator currencyValidator;

    public CurrencyServiceImpl(CurrencyRepository currencyRepository, CurrencyValidator currencyValidator) {
        this.currencyRepository = currencyRepository;
        this.currencyValidator = currencyValidator;
    }

    @Transactional(readOnly = true)
    @Override
    public CurrencyResponseDto findById(Long id) {
        return new CurrencyResponseDto(findCurrencyById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public Currency findCurrencyById(Long id) {
        return currencyRepository.findById(id)
                .orElseThrow(() -> new CustomException(INVALID_CURRENCY_EXCEPTION));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CurrencyResponseDto> findAll() {
        return currencyRepository.findAll().stream().map(CurrencyResponseDto::toDto).toList();
    }

    @Transactional
    @Override
    public CurrencyResponseDto save(CurrencyRequestDto currencyRequestDto) {
        try {

            // 환전 이름과, 표기가 맞는지 확인
            if (!currencyRequestDto.checkSymbol()) {
                throw new CustomException(SYMBOL_NOT_MATCH);
            }

            //DB에 환률 값이 음수 또는 0이면 안되기 때문에 예외처리가 필요하다고 생각
            //PostConstruct가 활용되는 걸 보기 위해 주석 처리
//            if (currencyRequestDto.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
//                throw new CustomError(CURRENCY_MUST_OVER_THAN_ZERO);
//            }

            Currency savedCurrency = currencyRepository.save(CurrencyRequestDto.toEntity(currencyRequestDto));
            return new CurrencyResponseDto(savedCurrency);
        } catch (IllegalArgumentException e) {
            throw new CustomException(CURRENCY_SAVE_FAILED);
        }
    }

    //인터페이스로 빼서 해도 괜찮지 않을까...?

    /**
     * 소수점을 비교하는 로직을 Transaction으로 처리할 필요가 있을까?에 대한 의문
     * interface로 빼서 해당 currency가 소수점을 반올림을 할지 버림을 할지에 대힌 비교만하기 때문에
     * 확장 가능성을 생각한다면 interface로 하는게 더 나은 방법이지 않을까?에 대한 의문
     *
     * 환율의 값이 0일경우 예외 처리
     * 소수점 자리수를 2자리 까지만 표현.
     */
//    @Transactional
//    @Override
//    public BigDecimal calculateExchangeAmount(BigDecimal amountInKrw, BigDecimal exchangeRate, boolean isWholeNumber) {
//        if (exchangeRate.compareTo(BigDecimal.ZERO) == 0) {
//            throw new CustomError(INVALID_CURRENCY_EXCEPTION);
//        }
//
//        int scale = isWholeNumber ? 0 : 2;
//        RoundingMode roundingMode = isWholeNumber ? RoundingMode.DOWN : RoundingMode.HALF_DOWN;
//
//        return amountInKrw.divide(exchangeRate, scale, roundingMode);
//    }
}
