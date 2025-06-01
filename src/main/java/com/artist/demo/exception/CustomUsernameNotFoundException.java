package com.artist.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class CustomUsernameNotFoundException extends RuntimeException {
    public CustomUsernameNotFoundException(String message) {
        super(message);
    }
}