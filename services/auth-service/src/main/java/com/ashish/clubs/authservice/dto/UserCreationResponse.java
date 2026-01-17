package com.ashish.clubs.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class UserCreationResponse {

    private String message;
    private String email;

    public UserCreationResponse(String message, String email) {
        this.message = message;
        this.email = email;
    }
}