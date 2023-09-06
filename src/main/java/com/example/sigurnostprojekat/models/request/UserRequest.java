package com.example.sigurnostprojekat.models.request;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String role;
    private String privateKey;
    private String publicKey;
}
