package com.ashish.clubs.common.models.shared;

/**
 * Generic status definitions that can be used across various entities or processes.
 * Specific enums (like MembershipStatus) can also exist for domain-specific statuses.
 */
public enum Status {
    ACTIVE,
    INACTIVE,
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED,
    DELETED,
    COMPLETED,
    FAILED,
    DRAFT,
    PUBLISHED,
    SUSPENDED,
    BANNED,
    EXPIRED
    // Add more as needed
}