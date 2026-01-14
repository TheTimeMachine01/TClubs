package com.ashish.clubs.apigateway.filter;

import com.ashish.clubs.common.security.SecurityConstants;
import com.ashish.clubs.common.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher; // Import AntPathMatcher
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List; // Ensure List is imported

/**
 * Custom GatewayFilter to validate JWT tokens.
 * This filter should be applied to routes that require authentication.
 */
@Component
@Slf4j
public class JwtAuthGatewayFilter extends AbstractGatewayFilterFactory<JwtAuthGatewayFilter.Config> {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher(); // For Ant-style path matching

    public JwtAuthGatewayFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // Check if the request path matches any public endpoint
            boolean isPublicEndpoint = Arrays.stream(SecurityConstants.PUBLIC_ENDPOINTS)
                    .anyMatch(uri -> antPathMatcher.match(uri, path));

            if (isPublicEndpoint) {
                log.debug("Path {} is a public endpoint, skipping JWT validation.", path);
                return chain.filter(exchange); // Continue to the next filter in the chain
            } else {
                log.debug("Path {} requires authentication, attempting JWT validation.", path);

                // Check for Authorization header
                if (!request.getHeaders().containsKey(SecurityConstants.JWT_HEADER_STRING)) {
                    log.warn("Missing Authorization header for secured path: {}", path);
                    return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);
                }

                String authHeader = request.getHeaders().getFirst(SecurityConstants.JWT_HEADER_STRING);

                // Check for Bearer token format
                if (authHeader == null || !authHeader.startsWith(SecurityConstants.JWT_TOKEN_PREFIX)) {
                    log.warn("Invalid Authorization header format for secured path: {}", path);
                    return this.onError(exchange, "Authorization header must start with Bearer", HttpStatus.UNAUTHORIZED);
                }

                String token = authHeader.substring(SecurityConstants.JWT_TOKEN_PREFIX.length());

                try {
                    // Extract username to implicitly validate token signature and expiration
                    String username = jwtUtil.extractUsername(token);
                    log.info("JWT token validated successfully for user: {}", username);

                    // If needed, add authenticated user info to request headers for downstream services
                    // Note: This is an example; in a real scenario, you might pass a dedicated DTO
                    // or validate roles more thoroughly here if the gateway is the only place doing it.
                    exchange.getRequest().mutate().header("X-Authenticated-User", username).build();

                } catch (ExpiredJwtException e) {
                    log.warn("JWT Token expired for path {}: {}", path, e.getMessage());
                    return this.onError(exchange, "JWT Token has expired", HttpStatus.UNAUTHORIZED);
                } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
                    log.warn("Invalid JWT Token for path {}: {}", path, e.getMessage());
                    return this.onError(exchange, "Invalid JWT Token", HttpStatus.UNAUTHORIZED);
                } catch (Exception e) {
                    log.error("An unexpected error occurred during JWT validation for path {}: {}", path, e.getMessage(), e);
                    return this.onError(exchange, "An unexpected error occurred during authentication", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            return chain.filter(exchange); // Continue to the next filter/route
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error("Gateway error response: {} - {}", httpStatus, err);
        return response.setComplete();
    }

    public static class Config {
        // No specific configuration properties needed for this filter, so Config is empty.
    }
}