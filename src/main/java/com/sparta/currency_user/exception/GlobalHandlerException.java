package com.sparta.currency_user.exception;

import com.sparta.currency_user.exception.type.ErrorType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomError(CustomException error) {
        ErrorType errorType = error.getErrorType();
        ErrorResponse response = new ErrorResponse(errorType.getErrorCode(), errorType.getMessage(), errorType.getStatus());
        return ResponseEntity
                .status(errorType.getStatus())
                .body(response);
    }
}
