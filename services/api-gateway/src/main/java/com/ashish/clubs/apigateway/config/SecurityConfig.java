package com.ashish.clubs.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Spring Security configuration for the API Gateway.
 * Configures authentication and authorization rules, integrating the custom JWT filter.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for stateless APIs, crucial for microservices
                .authorizeExchange(exchange ->
                    exchange
                            // Permit all POST requests to the login endpoint
                            .pathMatchers("/api/v1/auth/**").permitAll()
                            .pathMatchers("/actuator/**").permitAll()
                            // Add any other public paths here
                            // Example for a fallback route (if applicable)
                            .pathMatchers("/api/v1/fallback/**").permitAll()
                            .pathMatchers("/api/v1/users/**").permitAll()
                            // All other requests require authentication
                            .anyExchange().authenticated()
                );

        return http.build();
    }
}
