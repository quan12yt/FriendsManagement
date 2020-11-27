package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeAndBlockRequest {
    @NotEmpty(message = "Requester email must not be empty or null")
    private String requester;
    @NotEmpty(message = "Target email must not be empty or null")
    private String target;
}
