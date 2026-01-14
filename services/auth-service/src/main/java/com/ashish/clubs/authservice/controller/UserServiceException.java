package com.ashish.clubs.authservice.controller;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserServiceException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String originalMessage;

    public UserServiceException(HttpStatus httpStatus, String message, String originalMessage) {
        super(message); // Message for this exception
        this.httpStatus = httpStatus;
        this.originalMessage = originalMessage;
    }
}
