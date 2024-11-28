package com.sparta.currency_user.exchange.service;

import com.sparta.currency_user.currency.dto.CurrencyResponseDto;
import com.sparta.currency_user.currency.entity.Currency;
import com.sparta.currency_user.currency.service.CurrencyService;
import com.sparta.currency_user.exception.CustomError;
import com.sparta.currency_user.exchange.calculator.RoundExchangeCalculator;
import com.sparta.currency_user.exchange.dto.ExchangeRequestDto;
import com.sparta.currency_user.exchange.dto.ExchangeResponseDto;
import com.sparta.currency_user.exchange.dto.ExchangeSumDto;
import com.sparta.currency_user.exchange.entity.Exchange;
import com.sparta.currency_user.exchange.repository.ExchangeRepository;
import com.sparta.currency_user.type.CurrencyName;
import com.sparta.currency_user.exception.type.ErrorType;
import com.sparta.currency_user.type.ExchangeStatus;
import com.sparta.currency_user.user.entity.User;
import com.sparta.currency_user.user.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
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

    public ExchangeServiceImpl(EntityManager em, CurrencyService currencyService, ExchangeRepository exchangeRepository, UserService userService, RoundExchangeCalculator roundExchangeCalculator) {
        this.em = em;
        this.currencyService = currencyService;
        this.exchangeRepository = exchangeRepository;
        this.userService = userService;
        this.roundExchangeCalculator = roundExchangeCalculator;
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
        //통화 정보 조회
        CurrencyResponseDto currency = currencyService.findById(exchangeRequestDto.getCurrencyId());
        log.info("해당 통화 ID 가져오기 : {}", exchangeRequestDto.getCurrencyId());

        //소수점 처리 방식 결정
        boolean isWholeNumber = CurrencyName.valueOf(String.valueOf(currency.getCurrencyName())).isWholeNumber();
        log.info("소수점 여부 (wholeNumber): {}", isWholeNumber);

        //환전 계산 수행
        BigDecimal amountExchange = roundExchangeCalculator.calculateExchangeAmount
                (exchangeRequestDto.getAmountInKrw(), currency.getExchangeRate(), isWholeNumber);
        log.info("환율 값 가져오기: {}", currency.getExchangeRate());

        //환전 정보 저장
        Exchange exchange = saveExchange(exchangeRequestDto, amountExchange);
        log.info("환전 정보 저장 시작. 환전 후 금액: {}", amountExchange);

        String formattedAmount = exchange.getCurrency()
                .getCurrencyName()
                .formatAmount(amountExchange);

        log.info("환전 후 금액 : {}", formattedAmount);

        return ExchangeResponseDto.fromEntity(exchange);
    }

    /**
     * 환전을 진행한 id 값 조회
     * @Param exchangeId
     * @return 조회한 DTO
     */
    @Transactional(readOnly = true)
    @Override
    public ExchangeResponseDto getExchangeById(Long exchangeId) {
        Exchange exchangeById = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new CustomError(ErrorType.EXCHANGE_NOT_FOUND));

        return ExchangeResponseDto.fromEntity(exchangeById);
    }

    /**
     * user가 진행 했던 환전 내역 결과 표현
     * @Param userId 사용자 Id
     * @return 환전 내역을 포함한 DTO List
     */
    @Transactional(readOnly = true)
    @Override
    public List<ExchangeResponseDto> getExchanges(Long userId) {
        // 사용자 ID로 환전 내역 조회
        String sql = "select e from Exchange e where e.user.id = :userId";
        List<Exchange> exchanges = em.createQuery(sql, Exchange.class)
                .setParameter("userId", userId)
                .getResultList();

        if (exchanges.isEmpty()) {
            throw new IllegalArgumentException("해당 USER에서 환전 내역을 찾을 수 없습니다.");
        }

        return exchanges.stream().map(ExchangeResponseDto::fromEntity).collect(Collectors.toList());
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
     * @throws CustomError 업데이트된 데이터가 없을 경우 발생
     */
    @Transactional
    @Override
    public List<ExchangeResponseDto> updateExchangeStatus(Long userId, Long currencyId, ExchangeStatus exchangeStatus) {
        // 업데이트 쿼리 실행
        String jpql = "update Exchange e set e.exchangeStatus = :exchangeStatus " +
                "where e.user.id = :userId and e.currency.id = :currencyId";
        int update = em.createQuery(jpql)
                .setParameter("exchangeStatus", exchangeStatus)
                .setParameter("userId", userId)
                .setParameter("currencyId", currencyId)
                .executeUpdate();

        // 업데이트 실패 처리
        if (update == 0) {
            throw new CustomError(ErrorType.UPDATE_FAILED);
        }

        // 업데이트된 데이터 조회
        String selectSql = "select e from Exchange e where e.user.id = :userId and e.currency.id = :currencyId";
        List<Exchange> exchanges = em.createQuery(selectSql, Exchange.class)
                .setParameter("userId", userId)
                .setParameter("currencyId", currencyId)
                .getResultList();

        // DTO 변환 및 반환
        return exchanges.stream()
                .map(ExchangeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 환전 요청을 몇번 했는지 count & 총 금액 계산
     * @param userId 사용자 ID
     * @return 사용자 ID별 환전 횟수와 총 금액을 포함한 DTO
     */
    @Transactional(readOnly = true)
    @Override
    public ExchangeSumDto getExchangeSumDto(Long userId) {
        String jpql = "SELECT new com.sparta.currency_user.exchange.dto.ExchangeSumDto(COUNT(e), SUM(e.amountInKrw)) " +
                "FROM Exchange e " +
                "WHERE e.user.id = :userId " +
                "GROUP BY e.user.id";

        try {
            Query query = em.createQuery(jpql, ExchangeSumDto.class);
            query.setParameter("userId", userId);

            return (ExchangeSumDto) query.getSingleResult();
        } catch (NoResultException e) {
            throw new CustomError(ErrorType.USER_NOT_FOUND);
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
}
