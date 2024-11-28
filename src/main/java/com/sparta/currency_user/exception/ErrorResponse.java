package com.sparta.currency_user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String errorCode;
    private final String message;
    private final int status;
}
