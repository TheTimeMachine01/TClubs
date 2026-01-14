package com.ashish.clubs.userservice.entity; // This is in user-service's model package

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@Table("app_users") // Using a specific table name for clarity and to avoid 'user' keyword conflicts in DB
public class User {
    @Id
    private Long id;

    private String userId; // This is the UUID/String ID from common.models.User
    private String email;
    @Column("password_hash") // Map to snake_case column in DB if different
    private String passwordHash; // Stores the hashed password
    @Column("profile_picture_url")
    private String profilePictureUrl;

    // For simplicity, roles will be stored as a comma-separated String in the DB
    private String roles; // Changed from Set<Role> to String for DB persistence

    private boolean enabled;
    @Column("account_locked")
    private boolean accountLocked;
    @Column("created_at")
    private Instant createdAt;
    @Column("updated_at")
    private Instant updatedAt;
}