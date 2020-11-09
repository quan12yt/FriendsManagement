package com.example.demo.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeRequest {
    @NotNull(message = "Invalid Request body")
    private String requester;
    @NotNull(message = "Invalid Request body")
    private String target;
}
