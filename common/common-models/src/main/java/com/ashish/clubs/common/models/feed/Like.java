package com.ashish.clubs.common.models.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String likeId; // UUID
    private String postId; // Foreign key to Post
    private String userId; // userId of the user who liked
    private Instant timestamp;
}
