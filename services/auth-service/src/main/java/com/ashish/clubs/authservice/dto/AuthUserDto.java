package com.ashish.clubs.authservice.dto;

import com.ashish.clubs.common.models.user.Role;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

/**
 * For receiving user authentication data from user-services
 */

@Data
public class AuthUserDto {
    private String userId; // Matches userId in common.models.user.User
    private String email; // Matches email in common.models.user.User
    private String passwordHash; // <--- CRITICAL CHANGE: Matches passwordHash in common.models.user.User
    private String profilePictureUrl; // Matches profilePictureUrl
    private Set<Role> roles; // <--- CRITICAL CHANGE: Matches Set<Role> in common.models.user.User
    private boolean enabled;
    private boolean accountLocked;
    private Instant createdAt; // Or whatever type it is in common.models.user.User
    private Instant updatedAt; // Or whatever type it is in common.models.user.User
}
