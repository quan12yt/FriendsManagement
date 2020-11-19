package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InputInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage inputInvalidException(InputInvalidException ex, WebRequest webRequest) {
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value()
                , ex.getMessage()
                , new Date()
                , webRequest.getDescription(false));
    }

    @ExceptionHandler(WrongRequirementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage wrongStatus(WrongRequirementException ex, WebRequest webRequest) {
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value()
                , ex.getMessage()
                , new Date()
                , webRequest.getDescription(false));
    }
}
