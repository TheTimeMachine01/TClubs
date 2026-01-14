package com.ashish.clubs.userservice.controller;


import com.ashish.clubs.userservice.dto.UserCreationResponse;
import com.ashish.clubs.userservice.entity.User; // Import the internal User entity
import com.ashish.clubs.userservice.repository.UserRepository;
import com.ashish.clubs.userservice.service.UserService; // Import the UserService
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository; // Keep this for other methods if needed
    private final UserService userService; // Inject UserService

    // Constructor injection for both repositories and services (preferred)
    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/{id}") // <--- NEW/MODIFIED: For Auth Service to look up by ID for refresh tokens
    public Mono<com.ashish.clubs.common.models.user.User> getUserById(@PathVariable("id") String id) { // Ensure ID is String if it's a UUID
        log.debug("User Service: Fetching user by ID: {}", id);
        return userService.findByUserId(id) // Calls the new method in UserService
                .switchIfEmpty(Mono.error(() -> {
                    log.warn("User Service: User with ID {} not found.", id);
                    return new RuntimeException("User not found"); // Or custom UserNotFoundException
                }));
    }

    @GetMapping
    public Flux<User> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll(); // Using userRepository directly for simple retrieval
    }

    @GetMapping("/username/{email}") // This maps GET requests to /users/username/{email}
    public Mono<com.ashish.clubs.common.models.user.User> getUserByEmail(@PathVariable String email) {
        log.debug("Fetching user by email: {}", email);
        return userService.findByEmail(email); // Delegate to UserService to fetch and map
    }

    @GetMapping("/me") // Endpoint for currently signed-in user
    public Mono<ResponseEntity<com.ashish.clubs.common.models.user.User>> getAuthenticatedUser() {
        log.debug("User Service: Attempting to fetch details for authenticated user via /me");
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    if (authentication != null && authentication.isAuthenticated()) {
                        String email = authentication.getName(); // JWT subject (username/email)
                        log.debug("User Service: Authenticated user email: {}", email);
                        return userService.findByEmail(email)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).<com.ashish.clubs.common.models.user.User>build())
                                .onErrorResume(e -> {
                                    log.error("User Service: Error fetching /me for {}: {}", email, e.getMessage(), e);
                                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<com.ashish.clubs.common.models.user.User>build());
                                });
                    }
                    log.warn("User Service: Access to /me denied (unauthenticated).");
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).<com.ashish.clubs.common.models.user.User>build());
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).<com.ashish.clubs.common.models.user.User>build()); // In case context is empty
    }

    @PostMapping // This maps POST requests to /users
    public Mono<ResponseEntity<UserCreationResponse>> createUser(
            @RequestBody com.ashish.clubs.common.models.user.User userFromAuthService // Expect common.models.user.User
    ) {
        log.info("Attempting to create user from auth-service with email: {}", userFromAuthService.getEmail());
        // Delegate the complex creation logic to the UserService
        return userService.createUser(userFromAuthService)
                .map(savedUserEntity -> {
                    UserCreationResponse responseBody = new UserCreationResponse(
                            "User created successfully",
                            savedUserEntity.getEmail()
                    );
                    return ResponseEntity
                            .status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(responseBody);

                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    UserCreationResponse errorResponse = new UserCreationResponse(
                            "Signup error: " + e.getMessage(),
                            userFromAuthService.getEmail() // Still include email for context
                    );
                    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
                })
                .onErrorResume(e -> {
                    log.error("Generic error during user creation for {}: {}", userFromAuthService.getEmail(), e.getMessage());
                    UserCreationResponse errorResponse = new UserCreationResponse(
                            "Error during user creation: " + e.getMessage(),
                            userFromAuthService.getEmail()
                    );
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
                });
    }
}