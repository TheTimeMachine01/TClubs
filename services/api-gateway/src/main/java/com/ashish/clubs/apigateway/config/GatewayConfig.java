package com.ashish.clubs.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * Defines a KeyResolver bean for IP address based rate limiting.
 */
@Configuration
public class GatewayConfig {

    @Bean
    public KeyResolver ipAddressKeyResolver() {
        // Resolve the key based on the remote address of the request
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

    // You can add other gateway-specific configurations here if needed
}