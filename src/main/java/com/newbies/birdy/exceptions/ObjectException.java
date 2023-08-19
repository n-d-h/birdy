package com.newbies.birdy.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Setter
@Getter
public abstract class ObjectException {
    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime localDateTime;
}

