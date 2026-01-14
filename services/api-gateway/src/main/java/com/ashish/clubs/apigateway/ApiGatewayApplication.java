package com.ashish.clubs.apigateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@EnableDiscoveryClient // Enable Eureka client for service discovery
@ComponentScan(basePackages = {
        "com.ashish.clubs.apigateway", // Your gateway's own package
        "com.ashish.clubs.common.security.jwt", // <-- Corrected: Use your project's root package
        "com.ashish.clubs.common.config" // <-- Also ensure this is scanned if you have beans there
})
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    // CORS Configuration (Crucial for frontend applications)
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://your-frontend-domain.com")); // Replace with your frontend URLs
        corsConfig.setMaxAge(3600L); // How long the pre-flight request can be cached
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        corsConfig.setExposedHeaders(Collections.singletonList("Authorization")); // Expose Authorization header to clients
        corsConfig.setAllowCredentials(true); // Allow sending of cookies/credentials

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // Apply CORS to all paths
        return new CorsWebFilter(source);
    }
}
