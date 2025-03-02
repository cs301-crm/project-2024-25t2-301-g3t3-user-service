package com.cs301.crm.exceptions.handlers;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {ValidationException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(),
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                ), HttpStatus.BAD_REQUEST);
    }
}
