package com.ashish.clubs.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle fallback responses when a circuit breaker trips.
 */
@RestController
@RequestMapping("/api/v1/fallback") // Matches the fallbackUri in application.yml
public class FallbackController {

    @RequestMapping("/auth")
    public ResponseEntity<String> authServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Auth Service is currently unavailable. Please try again later.");
    }

    @RequestMapping("/user")
    public ResponseEntity<String> userServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/dummy")
    public ResponseEntity<String> dummyServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Dummy Service is currently unavailable. Please try again later.");
    }
}
