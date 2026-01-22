package com.ashish.clubs.services.feedservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "likes")
public class LikeDocument {
    @Id
    private String likeId;

    @Field("entity_id")
    private String entityId; // postId or commentId

    @Field("entity_type")
    private String entityType; // POST or COMMENT

    @Field("user_id")
    private String userId;

    private Instant timestamp;

    private Instant createdAt;
}

