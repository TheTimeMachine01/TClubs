package com.ashish.clubs.authservice.dto;

import lombok.Data;

@Data
public class UserCreationResponse {

    private String message;
    private String email;

    public UserCreationResponse(String message, String email) {
        this.message = message;
        this.email = email;
    }
}