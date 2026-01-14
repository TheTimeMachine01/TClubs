package com.ashish.clubs.authservice.dto.Signup;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
}
