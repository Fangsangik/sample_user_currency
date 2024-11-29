package com.sparta.currency_user.exception;

import com.sparta.currency_user.exception.type.ErrorType;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorType errorType;

    public CustomException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}
