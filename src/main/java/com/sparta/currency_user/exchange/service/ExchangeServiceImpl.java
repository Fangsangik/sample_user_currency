package com.sparta.currency_user.exchange.service;

import com.sparta.currency_user.currency.dto.CurrencyResponseDto;
import com.sparta.currency_user.currency.entity.Currency;
import com.sparta.currency_user.currency.service.CurrencyService;
import com.sparta.currency_user.exception.CustomException;
import com.sparta.currency_user.exchange.calculator.RoundExchangeCalculator;
import com.sparta.currency_user.exchange.dto.ExchangeRequestDto;
import com.sparta.currency_user.exchange.dto.ExchangeResponseDto;
import com.sparta.currency_user.exchange.dto.ExchangeSumDto;
import com.sparta.currency_user.exchange.entity.Exchange;
import com.sparta.currency_user.exchange.formatter.ExchangeFormatter;
import com.sparta.currency_user.exchange.repository.ExchangeRepository;
import com.sparta.currency_user.type.CurrencyName;
import com.sparta.currency_user.exception.type.ErrorType;
import com.sparta.currency_user.type.ExchangeStatus;
import com.sparta.currency_user.user.entity.User;
import com.sparta.currency_user.user.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExchangeServiceImpl implements ExchangeService {

    private final EntityManager em;
    private final CurrencyService currencyService;
    private final ExchangeRepository exchangeRepository;
    private final UserService userService;
    private final RoundExchangeCalculator roundExchangeCalculator;
    private final ExchangeFormatter exchangeFormatter;

    public ExchangeServiceImpl(EntityManager em, CurrencyService currencyService, ExchangeRepository exchangeRepository, UserService userService, RoundExchangeCalculator roundExchangeCalculator, ExchangeFormatter exchangeFormatter) {
        this.em = em;
        this.currencyService = currencyService;
        this.exchangeRepository = exchangeRepository;
        this.userService = userService;
        this.roundExchangeCalculator = roundExchangeCalculator;
        this.exchangeFormatter = exchangeFormatter;
    }

    /**
     * 환전을 진행하는 system
     * 1. 환전을 요청한 통화 아이디를 조회
     * 2. 해당 통화에 따라 소수점 버림 또는 반올림 판단
     * 3. 환전 진행 후 결과 값을 저장
     * 4. 저장된 결과 값을 반환
     *
     * @param exchangeRequestDto 환전 요청 정보
     * @return 환전 결과를 포함한 DTO
     */
    @Transactional
    @Override
    public ExchangeResponseDto performExchange(ExchangeRequestDto exchangeRequestDto) {
        // 통화 정보 조회
        CurrencyResponseDto currency = currencyService.findById(exchangeRequestDto.getCurrencyId());
        log.info("환전 요청 통화 ID: {}", exchangeRequestDto.getCurrencyId());

        // 환전 금액 계산
        BigDecimal amountExchange = calculateAmountAfterExchange(exchangeRequestDto.getAmountInKrw(), currency);

        // 환전 정보 저장
        Exchange exchange = saveExchange(exchangeRequestDto, amountExchange);
        log.info("환전 정보 저장 완료. 금액: {}", amountExchange);

        // 포맷된 금액 생성
        String formattedAmount = exchangeFormatter.formatAmount(amountExchange, currency);

        // DTO 변환 및 반환
        return ExchangeResponseDto.fromEntity(exchange, formattedAmount);
    }

    /**
     * 환전을 진행한 id 값 조회
     * @Param exchangeId
     * @return 조회한 DTO
     */
    @Transactional(readOnly = true)
    @Override
    public ExchangeResponseDto getExchangeById(Long exchangeId) {
        Exchange exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new CustomException(ErrorType.EXCHANGE_NOT_FOUND));

        // 포맷된 금액 생성
        String formattedAmount = exchangeFormatter.formatAmount(exchange.getAmountAfterExchange(), CurrencyResponseDto.toDto(exchange.getCurrency()));

        // DTO 변환 및 반환
        return ExchangeResponseDto.fromEntity(exchange, formattedAmount);
    }


    /**
     * user가 진행 했던 환전 내역 결과 표현
     * @Param userId 사용자 Id
     * @return 환전 내역을 포함한 DTO List
     */
    @Override
    @Transactional(readOnly = true)
    public List<ExchangeResponseDto> getExchanges(Long userId) {
        List<Exchange> exchanges = exchangeRepository.findByUserId(userId);

        if (exchanges.isEmpty()) {
            throw new CustomException(ErrorType.NO_HISTORY_FOUND);
        }

        // 포맷된 금액을 포함하여 DTO 생성 및 반환
        return exchanges.stream()
                .map(exchange -> {
                    String formattedAmount = exchangeFormatter.formatAmount(exchange.getAmountAfterExchange(), CurrencyResponseDto.toDto(exchange.getCurrency()));
                    return ExchangeResponseDto.fromEntity(exchange, formattedAmount);
                })
                .collect(Collectors.toList());
    }


    /**
     * 같은 userId로 여러번 환전 요청을 했을때 문제가 발생했다.
     * 처음 작성했을때는 List로 조회 하는 것이 아닌, getSingleResult로 값을 가져왔다. 그러기 때문에 같은 값으로 여러번 상태를 update 할때 문제가 발생
     * 그래서 List로 풀어낼 수 밖에 없었다 .
     *
     * @param userId 사용자 ID
     * @param currencyId 통화 ID
     * @param exchangeStatus 변경할 환전 상태
     * @return 업데이트된 환전 내역을 포함한 DTO 리스트
     * @throws CustomException 업데이트된 데이터가 없을 경우 발생
     */
    @Transactional
    @Override
    public List<ExchangeResponseDto> updateExchangeStatus(Long userId, Long currencyId, ExchangeStatus exchangeStatus) {
        int updatedCount = exchangeRepository.updateExchangeStatus(exchangeStatus, userId, currencyId);

        if (updatedCount == 0) {
            throw new CustomException(ErrorType.UPDATE_FAILED);
        }

        List<Exchange> updatedExchanges = exchangeRepository.findByUserIdAndCurrencyId(userId, currencyId);

        // 포맷된 금액을 포함하여 DTO 생성 및 반환
        return updatedExchanges.stream()
                .map(exchange -> {
                    BigDecimal recalculatedAmount = calculateAmountAfterExchange(exchange.getAmountInKrw(), CurrencyResponseDto.toDto(exchange.getCurrency()));
                    String formattedAmount = exchangeFormatter.formatAmount(recalculatedAmount, CurrencyResponseDto.toDto(exchange.getCurrency()));
                    return ExchangeResponseDto.fromEntity(exchange, formattedAmount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 환전 요청을 몇번 했는지 count & 총 금액 계산
     * @param userId 사용자 ID
     * @return 사용자 ID별 환전 횟수와 총 금액을 포함한 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public ExchangeSumDto getExchangeSumDto(Long userId) {
        try {
            return exchangeRepository.calculateExchangeSummary(userId);
        } catch (NoResultException e) {
            throw new CustomException(ErrorType.USER_NOT_FOUND);
        }
    }

    private Exchange saveExchange(ExchangeRequestDto exchangeRequestDto, BigDecimal amountExchange) {
        User user = userService.findUserById(exchangeRequestDto.getUserId());
        Currency currency = currencyService.findCurrencyById(exchangeRequestDto.getCurrencyId());

        Exchange exchange = Exchange.builder()
                .amountInKrw(exchangeRequestDto.getAmountInKrw())
                .amountAfterExchange(amountExchange)
                .user(user) // Lazy 로딩을 사용하려면 User 조회 필요
                .exchangeStatus(ExchangeStatus.NORMAL)
                .currency(currency) // Lazy 로딩을 사용하려면 Currency 조회 필요
                .build();

        exchangeRepository.save(exchange);
        return exchange;
    }

    private BigDecimal calculateAmountAfterExchange(BigDecimal amountInKrw, CurrencyResponseDto currency) {
        // 소수점 처리 방식 결정
        boolean isWholeNumber = CurrencyName.valueOf(currency.getCurrencyName().toString()).isWholeNumber();
        log.info("소수점 처리 여부: {}", isWholeNumber);

        // 환율 계산
        return roundExchangeCalculator.calculateExchangeAmount(amountInKrw, currency.getExchangeRate(), isWholeNumber);
    }
}
