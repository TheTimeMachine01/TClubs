package com.ashish.clubs.common.security;

public final class SecurityConstants {

    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER_STRING = "Authorization";

    // Public endpoints (e.g., for API Gateway to allow access without authentication)
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/**",
            "/eureka/**", // Eureka server endpoints
            "/actuator/**", // Spring Boot Actuator endpoints
            "/v3/api-docs/**", // OpenAPI/Swagger documentation
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**"
    };

    private SecurityConstants() {
        // restrict instantiation
    }
}