package com.example.demo.exception;

public class InputInvalidException extends RuntimeException {

    public InputInvalidException(String message) {
        super(message);
    }

    public InputInvalidException(String message, Throwable t) {
        super(message, t);
    }
}
