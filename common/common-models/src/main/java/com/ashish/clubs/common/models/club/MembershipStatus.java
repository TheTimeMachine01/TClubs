package com.ashish.clubs.common.models.club;

public enum MembershipStatus {
    ACTIVE,      // Currently an active member
    PENDING,     // Membership request is pending approval
    SUSPENDED,   // Membership temporarily suspended
    LEFT,        // User has left the club
    BANNED       // User has been banned from the club
}
