package com.ashish.clubs.authservice.dto.Login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for login responses.
 * Contains the JWT token issued upon successful authentication.
 */
@Data // Lombok annotation for getters, setters, equals, hashCode, toString
@NoArgsConstructor // Lombok annotation for no-argument constructor
@AllArgsConstructor // Lombok annotation for all-argument constructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;

    public LoginResponse(Object o, Object o1, String invalidCredentials) {
    }
}