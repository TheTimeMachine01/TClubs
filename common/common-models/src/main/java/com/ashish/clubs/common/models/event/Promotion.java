package com.ashish.clubs.common.models.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String promotionId; // UUID
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    private String createdByUserId;
    private Instant startDate;
    private Instant endDate;
    private String imageUrl;
    private String promotionCode; // Optional: for discount codes
    private String targetAudience; // e.g., "ALL_USERS", "CLUB_MEMBERS"
    private String status; // e.g., "ACTIVE", "EXPIRED", "DRAFT"
}
