package com.cs301.crm.exceptions.handlers;

import com.cs301.crm.exceptions.AwsException;
import com.cs301.crm.exceptions.InvalidTokenException;
import com.cs301.crm.exceptions.InvalidUserCredentials;
import com.cs301.crm.exceptions.JwtCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handlePoorInputs(MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            sb.append(errorMessage).append(", ");
        });
        return new ResponseEntity<>(
                new ErrorResponse(sb.substring(0, sb.length() - 2),
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleUsernameDoesNotExist(UsernameNotFoundException e) {
        return new ResponseEntity<>(
                new ErrorResponse("Username " + e.getMessage() + " not found",
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidUserCredentials.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(),
                        HttpStatus.UNAUTHORIZED,
                        ZonedDateTime.now()
                ), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {ExecutionException.class})
    public ResponseEntity<ErrorResponse> handleInvalidOtpRequest(Exception e) {
        return new ResponseEntity<>(
                new ErrorResponse("Invalid OTP request",
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                ), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = {HttpMessageNotReadableException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleBlankRequests() {

        return new ResponseEntity<>(
                new ErrorResponse("You are missing inputs in your request, please follow our API documentation",
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {Exception.class, AwsException.class, JwtCreationException.class})
    public ResponseEntity<ErrorResponse> handleException() {
        return new ResponseEntity<>(
                new ErrorResponse("Something went wrong on our end.",
                        HttpStatus.SERVICE_UNAVAILABLE,
                        ZonedDateTime.now()
                ), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = {InvalidTokenException.class})
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidTokenException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(),
                        HttpStatus.UNAUTHORIZED,
                        ZonedDateTime.now()
                ), HttpStatus.UNAUTHORIZED);
    }


}
