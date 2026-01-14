package com.ashish.clubs.userservice.dto;

import lombok.Data;

@Data
public class UserCreationResponse {

    private String message;
    private String email; // Or userId, if you prefer to return the ID

    public UserCreationResponse(String message, String email) {
        this.message = message;
        this.email = email;
    }

}
