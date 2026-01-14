package com.ashish.clubs.common.models.club;

public enum MembershipRole {
    MEMBER,     // Standard member
    ADMIN,      // Club administrator (distinct from system-wide ROLE_ADMIN)
    MODERATOR,  // Can moderate content within the club
    GUEST       // Limited access for non-members, if applicable
    // Add other club-specific roles as needed
}
