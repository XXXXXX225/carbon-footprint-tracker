package com.carbonfootprint.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String username;
    private String name;
    private String role;
}
