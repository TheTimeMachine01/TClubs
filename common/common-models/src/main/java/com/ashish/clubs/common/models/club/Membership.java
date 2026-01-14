package com.ashish.clubs.common.models.club;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Represents a user's membership in a specific club.
 * This entity links a user to a club and captures details about their membership.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membership implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Composite key or unique ID for the membership
    // In a database, this might be a composite primary key (clubId, userId)
    // For a common model, a simple unique ID is often sufficient for identification.
    private String membershipId; // Unique ID for this specific membership record

    private String clubId;   // ID of the club
    private String userId;   // ID of the user who is a member

    private MembershipRole role; // Role of the member within the club (e.g., MEMBER, ADMIN, MODERATOR)
    private Instant joinDate;   // When the user joined the club
    private Instant endDate;    // Optional: if membership expires or is revoked
    private MembershipStatus status; // e.g., ACTIVE, PENDING, SUSPENDED, LEFT

    // Add any other relevant membership details, e.g., specific permissions, points, etc.
}