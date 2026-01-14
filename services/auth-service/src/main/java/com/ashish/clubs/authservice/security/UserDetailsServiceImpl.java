package com.ashish.clubs.authservice.security;

import com.ashish.clubs.authservice.dto.AuthUserDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final WebClient.Builder webClientBuilder;
    private WebClient webClient;

    @Autowired
    private DiscoveryClient discoveryClient; // For service discovery (Eureka)

    @Value("${user-service.url}")
    private String userServiceUrl;

    public UserDetailsServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void init() {
        // This is where 'webClient' is supposed to be initialized
        this.webClient = webClientBuilder.baseUrl(userServiceUrl).build();
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // Make an HTTP call to user-service to get user details
        return webClient.get()
                .uri("/users/username/{username}", username)
                .retrieve()
                .bodyToMono(AuthUserDto.class)
                .map(authUserDto -> {
                        return (UserDetails) new CustomUserDetails(
                                authUserDto.getUserId(),
                                authUserDto.getEmail(),
                                authUserDto.getPasswordHash(),
                                authUserDto.getRoles().stream()
                                        .map(role -> new SimpleGrantedAuthority(role.name()))
                                        .collect(Collectors.toList())
                        );
                })
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)))
                .onErrorResume(e -> Mono.error(new UsernameNotFoundException("Error fetching user from user-service", e)));
    }

    // New method to find user by userId for refresh token use case
    public Mono<UserDetails> findByUserId(String userId) {
        return webClient.get()
                .uri("/users/{id}", userId) // User service must expose /users/{id}
                .retrieve()
                .bodyToMono(com.ashish.clubs.common.models.user.User.class) // Use common.models.user.User
                .map(user -> {
                    return (UserDetails) new CustomUserDetails(
                            user.getUserId(),
                            user.getEmail(),
                            user.getPasswordHash(),
                            user.getRoles().stream()
                                    .map(role -> new SimpleGrantedAuthority(role.name()))
                                    .collect(Collectors.toList())
                    );
                })
                .switchIfEmpty(Mono.error(() -> {
                    return new UsernameNotFoundException("User with ID " + userId + " not found.");
                }))
                .onErrorResume(e -> {
                    return Mono.error(new UsernameNotFoundException("Error fetching user by ID from user-service: " + e.getMessage(), e));
                });
    }
}
