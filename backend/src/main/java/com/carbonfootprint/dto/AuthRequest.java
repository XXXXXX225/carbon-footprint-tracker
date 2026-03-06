package com.carbonfootprint.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    private String name;
    private String email;
}
