package com.ashish.clubs.authservice.service;

import com.ashish.clubs.authservice.entity.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private ReactiveRedisTemplate<String, RefreshToken> reactiveRedisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:"; // A good prefix for keys

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration; // in milliseconds


    public Mono<RefreshToken> generateRefreshToken(String userId) {
        String token = UUID.randomUUID().toString();
        // Set expiry, e.g., 7 days from now
        Instant expiryDate = Instant.now().plusMillis(refreshExpiration);
        RefreshToken refreshToken = new RefreshToken(token, userId, expiryDate);

        // Store in Redis with an expiry
        // The key could be "refreshToken:theActualTokenString"
        return reactiveRedisTemplate.opsForValue()
                .set(REFRESH_TOKEN_PREFIX + token, refreshToken, Duration.between(Instant.now(), expiryDate))
                .thenReturn(refreshToken); // Return the token after saving
    }

    public Mono<RefreshToken> findRefreshToken(String token) {
        return reactiveRedisTemplate.opsForValue()
                .get(REFRESH_TOKEN_PREFIX + token);
    }

    public Mono<Void> deleteRefreshToken(String token) {
        return reactiveRedisTemplate.delete(REFRESH_TOKEN_PREFIX + token)
                .then(); // Convert Boolean result of delete to Mono<Void>
    }

    // You might also need a method to check if a token is expired (though Redis handles expiry)
    // or to update an existing token's expiry on refresh
}
