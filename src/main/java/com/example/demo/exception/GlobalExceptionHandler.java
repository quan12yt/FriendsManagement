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
        ErrorMessage message = new ErrorMessage();
        message.setTimestamp(new Date());
        message.setStatusCode(HttpStatus.BAD_REQUEST.value());
        message.setMessage(ex.getMessage());
        message.setDescription(webRequest.getDescription(false));
        return message;
    }

    @ExceptionHandler(WrongRequirementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage wrongStatus(WrongRequirementException ex, WebRequest webRequest) {
        ErrorMessage message = new ErrorMessage();
        message.setTimestamp(new Date());
        message.setStatusCode(HttpStatus.BAD_REQUEST.value());
        message.setMessage(ex.getMessage());
        message.setDescription(webRequest.getDescription(false));
        return message;
    }
}
