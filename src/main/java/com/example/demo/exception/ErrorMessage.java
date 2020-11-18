package com.example.demo.exception;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorMessage {
    private Integer statusCode;
    private String message;
    private Date timestamp;
    private String description;
}
