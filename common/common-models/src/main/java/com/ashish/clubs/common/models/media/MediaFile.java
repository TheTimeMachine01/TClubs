package com.ashish.clubs.common.models.media;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Represents metadata for a stored media file (image, video).
 * The actual file content would be stored in S3/MinIO/etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String mediaId; // UUID
    private String originalFileName;
    private String storedFileName; // Unique name in storage
    private String fileType; // e.g., "image/jpeg", "video/mp4"
    private long fileSize; // in bytes
    private String url; // Accessible URL for the file
    private String uploadedByUserId;
    private Instant uploadTimestamp;
    private String associatedEntityType; // e.g., "POST", "USER_PROFILE", "CLUB"
    private String associatedEntityId; // ID of the entity it's associated with
}
