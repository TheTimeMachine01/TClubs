package com.ashish.clubs.userservice.service;


import com.ashish.clubs.common.models.user.Role; // Import Role enum from common-models
import com.ashish.clubs.userservice.entity.User;
import com.ashish.clubs.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // This method accepts the User object from common-models as input
    public Mono<User> createUser(com.ashish.clubs.common.models.user.User commonUser) {
        // Check if a user with this email already exists
        return userRepository.findByEmail(commonUser.getEmail())
                .flatMap(existingUser -> Mono.<User>error(new IllegalArgumentException("User with email '" + commonUser.getEmail() + "' already exists.")))
                .switchIfEmpty(Mono.defer(() -> {
                    User userToSave = new User();
                    userToSave.setUserId(UUID.randomUUID().toString()); // Generate a unique userId for the internal entity
                    userToSave.setEmail(commonUser.getEmail());
                    userToSave.setPasswordHash(commonUser.getPasswordHash()); // Use the hashed password
                    userToSave.setProfilePictureUrl(commonUser.getProfilePictureUrl());

                    // Convert Set<Role> from common.models.User to comma-separated String for internal User entity
                    if (commonUser.getRoles() != null && !commonUser.getRoles().isEmpty()) {
                        String rolesString = commonUser.getRoles().stream()
                                .map(Role::name) // Get the string name of each Role enum
                                .collect(Collectors.joining(","));
                        userToSave.setRoles(rolesString);
                    } else {
                        userToSave.setRoles(Role.ROLE_USER.name()); // Default role if none provided
                    }

                    userToSave.setEnabled(true); // Default to enabled
                    userToSave.setAccountLocked(false); // Default to not locked
                    userToSave.setCreatedAt(Instant.now());
                    userToSave.setUpdatedAt(Instant.now());

                    // Save the user to the database
                    return userRepository.save(userToSave);
                }).cast(User.class));
    }

    // Method to find a user by email and return as common.models.user.User (DTO for auth-service)
    public Mono<com.ashish.clubs.common.models.user.User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToCommonUser); // Map internal User entity to common.models.user.User
    }

    // <--- NEW: Method to find a user by their UUID userId
    public Mono<com.ashish.clubs.common.models.user.User> findByUserId(String userId) {
        return userRepository.findByUserId(userId) // This needs to be a method in your UserRepository
                .map(this::mapToCommonUser);
    }

    // Helper method to map internal User entity to common.models.user.User (for responses/auth-service)
    private com.ashish.clubs.common.models.user.User mapToCommonUser(User internalUser) {
        Set<Role> roles = Collections.emptySet();
        if (internalUser.getRoles() != null && !internalUser.getRoles().isEmpty()) {
            roles = Arrays.stream(internalUser.getRoles().split(","))
                    .map(Role::valueOf) // Convert string back to Role enum
                    .collect(Collectors.toSet());
        }

        return com.ashish.clubs.common.models.user.User.builder()
                .userId(internalUser.getUserId())
                .email(internalUser.getEmail())
                .passwordHash(internalUser.getPasswordHash())
                .profilePictureUrl(internalUser.getProfilePictureUrl())
                .roles(roles)
                .enabled(internalUser.isEnabled())
                .accountLocked(internalUser.isAccountLocked())
                .createdAt(internalUser.getCreatedAt())
                .updatedAt(internalUser.getUpdatedAt())
                .build();
    }
}