package com.ashish.clubs.common.security;

import com.ashish.clubs.common.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.security.core.context.ReactiveSecurityContextHolder; // Reactive context
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * A common JWT authentication filter that can be used by services.
 * It extracts the JWT token, validates it, and sets the Spring Security context.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final ReactiveUserDetailsService reactiveUserDetailsService; // This will be provided by specific services

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith(SecurityConstants.JWT_TOKEN_PREFIX)) {
            return chain.filter(exchange);
        }

        jwt = authHeader.substring(SecurityConstants.JWT_TOKEN_PREFIX.length());
        username = jwtUtil.extractUsername(jwt);

        if (username != null) {

            return reactiveUserDetailsService.findByUsername(username)
                    .flatMap(userDetails -> {
                        if (Boolean.TRUE.equals(jwtUtil.validateToken(jwt, userDetails).block())) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null, // credentials are null as token is already validated
                                    userDetails.getAuthorities()
                            );
                            // Set authentication in reactive context
                            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                        }
                        return chain.filter(exchange); // Token invalid, continue chain
                    })
                    .switchIfEmpty(chain.filter(exchange)); // User not found, continue chain
        }
        return chain.filter(exchange); // No username, continue chain
    }
}
