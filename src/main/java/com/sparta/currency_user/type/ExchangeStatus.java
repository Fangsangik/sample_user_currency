package com.sparta.currency_user.type;

public enum ExchangeStatus {
    NORMAL("정상상태"),
    CANCELLED("취소"),
    PENDING("대기중");

    private String message;

    ExchangeStatus(String message) {
        this.message = message;
    }
}
