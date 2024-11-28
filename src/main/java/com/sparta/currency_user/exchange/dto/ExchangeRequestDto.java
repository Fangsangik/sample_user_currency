package com.sparta.currency_user.exchange.dto;

import com.sparta.currency_user.type.ExchangeStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequestDto {
    @NotNull(message = "사용자 ID는 필수 입력 값입니다.")
    private Long userId; // User ID

    @NotNull(message = "통화 ID는 필수 입력 값입니다.")
    private Long currencyId; // Currency ID

    @NotNull(message = "환전 금액은 필수 입력 값입니다.")
    @Positive(message = "환전 금액은 0보다 커야 합니다.")
    private BigDecimal amountInKrw; // 환전 전 금액

    @NotNull(message = "환전 상태는 필수 입력 값입니다.")
    private ExchangeStatus exchangeStatus; // Enum으로 정의된 상태
}
