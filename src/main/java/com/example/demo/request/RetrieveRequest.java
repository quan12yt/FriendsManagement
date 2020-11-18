package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveRequest {
    @NotEmpty(message = "Sender email must not be null or empty")
    private String sender;
    @NotEmpty(message = "Text must not be null or empty")
    private String text;
}
