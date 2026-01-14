package com.ashish.clubs.common.models.club;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Represents a club created by users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Club implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String clubId; // UUID
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;
    @NotBlank
    private String description;
    private String ownerId; // userId of the owner
    private String imageUrl; // URL to the club's profile image
    private String location; // Optional: for physical clubs
    private String status; // e.g., "ACTIVE", "INACTIVE", "PENDING_APPROVAL"
    private Instant createdAt;
    private Instant updatedAt;
}
