package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String success;
    private String userName;
    private List<GrantedAuthority> roles;
    private String token;
}
