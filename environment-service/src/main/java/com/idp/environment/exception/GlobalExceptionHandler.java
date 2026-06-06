package com.idp.environment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            RuntimeException.class
    )
    @ResponseStatus(
            HttpStatus.BAD_REQUEST
    )
    public String handle(
            RuntimeException e) {

        return e.getMessage();
    }
}