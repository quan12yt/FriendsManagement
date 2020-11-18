package com.example.demo.exception;

public class WrongRequirementException extends RuntimeException {

    public WrongRequirementException(String message) {
        super(message);
    }

    public WrongRequirementException(String message, Throwable t) {
        super(message, t);
    }
}
