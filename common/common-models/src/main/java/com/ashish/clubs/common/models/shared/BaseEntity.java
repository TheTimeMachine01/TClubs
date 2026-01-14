package com.ashish.clubs.common.models.shared;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Provides common fields for entities, such as ID, creation timestamp,
 * and last update timestamp. Entities can extend this class to inherit
 * these common attributes.
 */
@Data
@NoArgsConstructor
@SuperBuilder // Use SuperBuilder for inheritance with Lombok @Builder
public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected String id; // Universal ID, e.g., UUID
    protected Instant createdAt;
    protected Instant updatedAt;
    protected Integer version; // For optimistic locking, if using JPA/Hibernate
}
