package com.example.sigurnostprojekat.models.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String username;
    private String accessToken;
    private String tokenType = "Bearer ";

    public AuthResponse(String accessToken, String username) {
        this.accessToken = accessToken;
        this.username = username;
    }
}
