package com.example.demo.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    @NotEmpty(message = "Email mustn't be empty or null")
    private String email;
}
