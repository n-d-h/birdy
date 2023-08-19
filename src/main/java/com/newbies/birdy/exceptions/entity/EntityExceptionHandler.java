package com.newbies.birdy.exceptions.entity;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class EntityExceptionHandler {

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<Object> handleException(InsufficientAuthenticationException e,
                                                    HttpServletRequest request) {
        EntityException apiError = new EntityException(
                e.getMessage(),
                HttpStatus.FORBIDDEN,
                ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
        );

        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleException(BadCredentialsException e,
                                                    HttpServletRequest request) {
        EntityException apiError = new EntityException(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED,
                ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
        );

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        EntityException exception = new EntityException(
                e.getMessage(),
                notFound,
                ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
        );
        return new ResponseEntity<>(exception, notFound);
    }

    @ExceptionHandler(value = {QuantityException.class})
    public ResponseEntity<Object> handleQuantityException(EntityNotFoundException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        EntityException exception = new EntityException(
                e.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
        );
        return new ResponseEntity<>(exception, badRequest);
    }

}

