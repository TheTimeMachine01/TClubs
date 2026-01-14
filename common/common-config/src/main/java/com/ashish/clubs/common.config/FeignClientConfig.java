package com.ashish.clubs.common.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Common Feign client configuration.
 * Services can import this configuration or define their own specific ones.
 */
@Configuration
public class FeignClientConfig {

    // Configure Feign Logger level (e.g., NONE, BASIC, HEADERS, FULL)
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // Log full request and response for debugging
    }

    // Common error decoder for Feign clients
    // This example uses the default, but you can implement custom logic here
    @Bean
    public ErrorDecoder errorDecoder() {
        return new ErrorDecoder.Default();
    }

    // Example: A request interceptor to pass common headers (e.g., correlation ID)
    // You might move this to common-security if it's security-related (e.g., JWT propagation)
    @Bean
    public RequestInterceptor commonRequestInterceptor() {
        return requestTemplate -> {
            // Example: Add a correlation ID header for distributed tracing
            // String correlationId = MDC.get("correlationId"); // Requires Spring Cloud Sleuth or similar
            // if (correlationId != null) {
            //     requestTemplate.header("X-Correlation-ID", correlationId);
            // }
            // Example: Propagate JWT token (more suitable for common-security)
            // String jwt = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
            // requestTemplate.header("Authorization", "Bearer " + jwt);
        };
    }
}
