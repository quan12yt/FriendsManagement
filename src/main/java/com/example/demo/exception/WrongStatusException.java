package com.example.demo.exception;

public class WrongStatusException extends RuntimeException {

    public WrongStatusException(String message) {
        super(message);
    }

    public WrongStatusException(String message, Throwable t) {
        super(message, t);
    }
}
