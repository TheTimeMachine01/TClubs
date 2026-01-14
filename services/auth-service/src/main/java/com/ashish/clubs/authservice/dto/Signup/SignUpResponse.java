package com.ashish.clubs.authservice.dto.Signup;

import lombok.Data;

@Data
public class SignUpResponse {

    private String message;

    public SignUpResponse(String message) {
        this.message = message; // Assign the incoming message to the 'message' field
    }
}
