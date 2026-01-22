package com.ashish.clubs.services.mediaservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("media_files")
public class MediaFileEntity {
    @Id
    @Column("media_id")
    private UUID mediaId;

    @Column("file_name")
    private String fileName;

    @Column("file_size")
    private Long fileSize;

    @Column("file_type")
    private String fileType;

    @Column("mime_type")
    private String mimeType;

    @Column("uploader_id")
    private UUID uploaderId;

    @Column("entity_type")
    private String entityType;

    @Column("entity_id")
    private String entityId;

    @Column("s3_key")
    private String s3Key;

    @Column("minio_path")
    private String minioPath;

    private String url;

    private String status;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}


