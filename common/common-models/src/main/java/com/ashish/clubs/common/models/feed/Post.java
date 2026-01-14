package com.ashish.clubs.common.models.feed;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * Represents a post in the feed.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String postId; // UUID
    @NotBlank
    private String content;
    private String authorId; // userId of the author
    private Instant timestamp;
    private List<String> mediaUrls; // URLs to images/videos for the post
    private int likeCount; // Denormalized for quick access, updated via events
    private int commentCount; // Denormalized for quick access
    // Add tags, category, etc.
}
