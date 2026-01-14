package com.ashish.clubs.common.models.feed;

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
public class Comment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String commentId; // UUID
    private String postId; // Foreign key to Post
    private String authorId; // userId of the author
    @NotBlank
    private String content;
    private Instant timestamp;
}