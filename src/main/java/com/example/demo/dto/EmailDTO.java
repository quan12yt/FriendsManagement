package com.example.demo.dto;

import com.example.demo.model.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO {

    private Long emailId;
    @NotEmpty(message = "Email mustn't be empty or null")
    private String email;
    private Set<Email> friends;
}
