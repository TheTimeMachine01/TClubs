package com.ashish.clubs.common.models.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String eventId; // UUID
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    private String creatorId; // userId of the creator
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String imageUrl; // URL for event banner
    private String type; // e.g., "PROMOTION", "MEETING", "WORKSHOP"
    private String status; // e.g., "SCHEDULED", "CANCELLED", "COMPLETED"
    // private Instant createdAt;
    // private Instant updatedAt;
}
