package com.newbies.birdy.exceptions.entity;

import com.newbies.birdy.exceptions.ObjectException;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class EntityException extends ObjectException {
    public EntityException(String message, HttpStatus httpStatus, ZonedDateTime timeZone) {
        super(message, httpStatus, timeZone);
    }
}
