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
@Document(collection = "comments")
public class CommentDocument {
    @Id
    private String commentId;

    @Field("post_id")
    private String postId;

    @Field("author_id")
    private String authorId;

    private String content;

    private Instant timestamp;

    @Field("like_count")
    private long likeCount;

    private String status; // DRAFT, PUBLISHED, DELETED

    private Instant createdAt;

    private Instant updatedAt;
}

