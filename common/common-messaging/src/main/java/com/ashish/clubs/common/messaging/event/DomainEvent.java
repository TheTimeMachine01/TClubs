package com.ashish.clubs.common.messaging.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Base event class for all Kafka events in the system.
 * All domain events should extend or use this structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainEvent {

    @JsonProperty("event_id")
    private String eventId; // UUID of the event

    @JsonProperty("event_type")
    private String eventType; // e.g., CLUB_CREATED, POST_LIKED

    @JsonProperty("source_service")
    private String sourceService; // Which service emitted this event

    @JsonProperty("entity_id")
    private String entityId; // ID of the entity (clubId, postId, etc.)

    @JsonProperty("entity_type")
    private String entityType; // Type of entity (CLUB, POST, USER, etc.)

    @JsonProperty("actor_id")
    private String actorId; // User who triggered the event

    @JsonProperty("payload")
    private Object payload; // Event-specific data

    @JsonProperty("timestamp")
    private Instant timestamp;

    @Default
    @JsonProperty("version")
    private int version = 1; // Event schema version
}

