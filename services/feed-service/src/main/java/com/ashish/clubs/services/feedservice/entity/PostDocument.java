package com.ashish.clubs.services.feedservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "posts")
public class PostDocument {
    @Id
    private UUID postId;
    private String clubId;
    private String userId;
    private String content;
    private List<String> mediaUrls;
    private long likeCount;
    private long commentCount;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}

