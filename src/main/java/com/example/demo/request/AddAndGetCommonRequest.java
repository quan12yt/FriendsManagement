package com.example.demo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddAndGetCommonRequest {
    @NotNull(message = "List email must not be null or empty")
    private List<String> friends;
}
