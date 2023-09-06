package com.example.sigurnostprojekat.models.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
