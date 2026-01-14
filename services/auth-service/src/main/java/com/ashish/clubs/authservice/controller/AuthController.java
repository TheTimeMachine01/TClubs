package com.ashish.clubs.authservice.controller;

import com.ashish.clubs.authservice.dto.Login.LoginRequest;
import com.ashish.clubs.authservice.dto.Login.LoginResponse;
import com.ashish.clubs.authservice.dto.Login.RefreshRequest;
import com.ashish.clubs.authservice.dto.Signup.SignUpResponse;
import com.ashish.clubs.authservice.dto.Signup.SignupRequest;
import com.ashish.clubs.authservice.dto.UserCreationResponse;
import com.ashish.clubs.authservice.entity.RefreshToken;
import com.ashish.clubs.authservice.security.CustomUserDetails;
import com.ashish.clubs.authservice.security.UserDetailsServiceImpl;
import com.ashish.clubs.authservice.service.RefreshTokenService;
import com.ashish.clubs.common.models.user.Role;
import com.ashish.clubs.common.models.user.User;
import com.ashish.clubs.common.security.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth") // Note: The Gateway prefixes this with /api/v1/auth
public class AuthController {

    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private WebClient userServiceWebClient;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService; // To get the actual user ID

    @PostMapping("/signup")
    public Mono<ResponseEntity<SignUpResponse>> signUp(@RequestBody SignupRequest signUpRequest) {
        String hashedPassword = passwordEncoder.encode(signUpRequest.getPassword());

        User userToRegister = User.builder() // Use builder from common-models.User
                .email(signUpRequest.getEmail()) // Use getEmail() from updated SignupRequest
                .passwordHash(hashedPassword)
                .enabled(true)
                .accountLocked(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .roles(Set.of(Role.ROLE_USER)) // Assuming you import Role from common-models
                .build();


        return userServiceWebClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userToRegister)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Client Error: " + clientResponse.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Server Error: " + clientResponse.statusCode())))
                .bodyToMono(UserCreationResponse.class)
                .map(userCreatedResponse -> {
                            System.out.println("User created successfully. ID: " + userCreatedResponse.getEmail() + ", Message: " + userCreatedResponse.getMessage());
                            return ResponseEntity.status(HttpStatus.CREATED).body(new SignUpResponse("User registered successfully:" + userCreatedResponse.getMessage()));
                })
                .onErrorResume(e -> {
                            System.err.println("Error during signup in AuthController: " + e.getMessage()); // Log for debugging
                            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body(new SignUpResponse("Error during signup: " + e.getMessage())));
                });
    }

    /**
     * Handles user login and issues access and refresh tokens upon successful authentication.
     *
     * @param loginRequest DTO containing user credentials (email and password).
     * @return A Mono emitting a ResponseEntity with LoginResponse (access and refresh tokens)
     * or an error status if authentication fails.
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
                )
                .flatMap(authentication -> {

                    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                    String userId = userDetails.getUserId();
                    String email = userDetails.getUsername(); // This is the email
                    Set<String> roles = userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toSet());

                    // Generate access token using common JwtUtil
                    Mono<String> accessTokenMono = jwtUtil.generateAccessToken(email, roles);

                    // Generate and store refresh token
                    Mono<RefreshToken> refreshTokenMono = refreshTokenService.generateRefreshToken(userId);

                    return Mono.zip(accessTokenMono, refreshTokenMono);
                })
                .map(tuple -> {
                    String accessToken = tuple.getT1();
                    RefreshToken refreshToken = tuple.getT2();
                    return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken.getToken()));
                })
                .onErrorResume(e -> {
                    // Provide a more specific message if it's a known authentication error
                    e.printStackTrace();
                    if (e instanceof AuthenticationException) {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(null, null, "Invalid credentials")));
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(null, null, "Authentication failed")));
                });
    }

    /**
     * Handles refresh token requests to issue a new access token and a new refresh token.
     *
     * @param refreshTokenRequest DTO containing the old refresh token.
     * @return A Mono emitting a ResponseEntity with LoginResponse (new access and refresh tokens)
     * or an error status if the token is invalid/expired/not found.
     */
    @PostMapping("/refresh")
    public Mono<ResponseEntity<LoginResponse>> refreshToken(@RequestBody RefreshRequest refreshTokenRequest) {

        final String oldRefreshToken = refreshTokenRequest.getRefreshToken();

        // 1. Find the refresh token in redis
        return refreshTokenService.findRefreshToken(oldRefreshToken).flatMap(foundToken -> {
            // 2. Validate token (eg., not revoked, not expired)
            // The expiry check is primarily done by Redis TTL, but an explicit check here is good.
            // Also check the 'revoked' status.
            if (foundToken == null || foundToken.isRevoked() || foundToken.getExpiryDate().isBefore(Instant.now())) {
                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(null, null, "Invalid or expired refresh token")));
            }

            // 3. Invalidate the old refresh token
            return refreshTokenService.deleteRefreshToken(oldRefreshToken).then(Mono.defer(() -> { // Use Mono.defer to ensure operations are lazy
                // 4. Generate new refresh token
                String userId = foundToken.getUserId(); // Get user ID from the found refresh token
                return refreshTokenService.generateRefreshToken(userId).flatMap(newRefreshTokenEntity -> {
                    // 5. Generate new access token
                    // You'll need to fetch user roles here, possibly from the user service
                    // For this example, let's assume a default role or fetch from user service
                    // Replace this with actual user role fetching logic
                    Mono<Set<String>> userRolesMono = Mono.just(Collections.singleton("USER")); // Placeholder: fetch actual user roles
                    // If you need to get roles from the user-service, you would call it here:
                    // return userService.getUserRoles(userId).flatMap(roles -> { ... });

                    return userRolesMono.flatMap(roles ->
                        jwtUtil.generateAccessToken(userId, roles) // Generate new access token
                            .map(newAccessToken -> {
                                // 6. Construct and return the new LoginResponse DTO
                                return ResponseEntity.ok(new LoginResponse(newAccessToken, newRefreshTokenEntity.getToken()));
                            })
                    );
                });
            }));
        })
        .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new LoginResponse(null, null, "Refresh token not found"))));
    }

    /**
     * Handles user logout by invalidating the provided refresh token.
     *
     * @param refreshToken DTO containing the refresh token to invalidate.
     * @return A Mono emitting a ResponseEntity indicating success or failure of logout.
     */
    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(@RequestBody RefreshRequest refreshToken) {
        return refreshTokenService.deleteRefreshToken(refreshToken.getRefreshToken())
                .map(voidResult -> ResponseEntity.ok("Logged out successfully"))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Logout failed: " + e.getMessage())));
    }
}
