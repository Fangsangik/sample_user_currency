package com.sparta.currency_user.exception.type;

import lombok.Getter;

@Getter
public enum ErrorType {
    //USER
    USER_NOT_FOUND("ERR001", "해당 회원을 찾을 수 없습니다.", 404),
    CREATE_FAILURE("ERR002", "회원을 생성하는 도중 문제가 발생했습니다.", 400),
    DELETE_FAILED("ERR003", "회원을 삭제하는 도중 문제가 발생했습니다.", 400),

    //CURRENCY
    CURRENCY_NOT_FOUND("ERR004", "해당 통화를 찾을 수 없습니다.", 400),
    CURRENCY_SAVE_FAILED("ERR005", "통화를 저장하는데 실패했습니다.", 400),
    INVALID_CURRENCY_EXCEPTION("ER006", "유효하지 않은 통화 입니다.", 404),

    //EXCHANGE
    EXCHANGE_FAILED("ERR006", "환전 금액은 0보다 커야 합니다.", 400),
    EXCHANGE_STATUS_NOT_FOUND("ERR007", "진행 상황을 찾을 수 업습니다.", 404),
    UPDATE_FAILED("ERR008", "update를 실패했습니다.", 400),
    EXCHANGE_NOT_FOUND("ERR009", "해당 exchange 값을 찾을 수 없습니다." , 404),

    //CurrencyName
    SYMBOL_NOT_MATCH("ERRO10", "표기가 일치하지 않습니다." , 400),
    CURRENCY_MUST_OVER_THAN_ZERO("ERRO011", "환율은 0보다 커야 합니다." , 400),
    NO_HISTORY_FOUND("ERR012", "해당 이력이 없습니다." , 404);

    private String errorCode;
    private String message;
    private int status;

    ErrorType(String errorCode, String message, int status) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
    }
}
