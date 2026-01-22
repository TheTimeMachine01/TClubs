package com.ashish.clubs.services.mediaservice.service;

import com.ashish.clubs.common.config.AppConstants;
import com.ashish.clubs.common.messaging.event.DomainEvent;
import com.ashish.clubs.common.messaging.producer.KafkaEventProducer;
import com.ashish.clubs.common.models.media.MediaFile;
import com.ashish.clubs.services.mediaservice.entity.MediaFileEntity;
import com.ashish.clubs.services.mediaservice.repository.MediaFileRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.messages.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaFileRepository mediaFileRepository;
    private final KafkaEventProducer kafkaEventProducer;
    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * Upload a file to MinIO
     */
    public Mono<MediaFile> uploadFile(FilePart filePart, String uploaderId, String entityType, String entityId) {
        log.info("Uploading file: {} for entity: {}", filePart.filename(), entityType);

        String mediaId = UUID.randomUUID().toString();
        String minioPath = String.format("%s/%s", entityType.toLowerCase(), mediaId);

        return filePart.content()
                .collectList()
                .map(dataBuffers -> {
                    int totalSize = dataBuffers.stream()
                            .mapToInt(dataBuffer -> dataBuffer.readableByteCount())
                            .sum();

                    byte[] fileBytes = new byte[totalSize];
                    int pos = 0;
                    for (var dataBuffer : dataBuffers) {
                        int readableByteCount = dataBuffer.readableByteCount();
                        dataBuffer.read(fileBytes, pos, readableByteCount);
                        pos += readableByteCount;
                    }
                    return fileBytes;
                })
                .publishOn(Schedulers.boundedElastic())
                .flatMap(fileBytes -> {
                    try {
                        // Upload to MinIO
                        minioClient.putObject(
                                PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(minioPath)
                                        .stream(new ByteArrayInputStream(fileBytes), fileBytes.length, -1)
                                        .contentType(filePart.headers().getContentType() != null ?
                                                filePart.headers().getContentType().toString() : "application/octet-stream")
                                        .build()
                        );

                        // Create database entry
                        MediaFileEntity entity = MediaFileEntity.builder()
                                .mediaId(UUID.fromString(mediaId))
                                .fileName(filePart.filename())
                                .fileSize((long) fileBytes.length)
                                .mimeType(filePart.headers().getContentType() != null ?
                                        filePart.headers().getContentType().toString() : "application/octet-stream")
                                .uploaderId(UUID.fromString(uploaderId))
                                .entityType(entityType)
                                .entityId(entityId)
                                .minioPath(minioPath)
                                .url(String.format("%s/%s/%s", "minio", bucketName, minioPath))
                                .status("ACTIVE")
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build();

                        return mediaFileRepository.save(entity);
                    } catch (Exception e) {
                        log.error("Error uploading file to MinIO", e);
                        return Mono.error(e);
                    }
                })
                .flatMap(savedEntity -> {
                    MediaFile mediaFile = entityToModel(savedEntity);

                    // Publish MEDIA_UPLOADED event
                    DomainEvent event = DomainEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("MEDIA_UPLOADED")
                            .sourceService("media-service")
                            .entityId(mediaId)
                            .entityType("MEDIA_FILE")
                            .actorId(uploaderId)
                            .payload(mediaFile)
                            .timestamp(Instant.now())
                            .build();

                    return kafkaEventProducer.publishEvent(AppConstants.KAFKA_TOPIC_ANALYTICS_EVENTS, event)
                            .thenReturn(mediaFile)
                            .doOnError(ex -> log.error("Failed to publish media upload event", ex));
                });
    }

    /**
     * Get media file by ID
     */
    public Mono<MediaFile> getMediaFileById(UUID mediaId) {
        return mediaFileRepository.findByMediaId(mediaId)
                .map(this::entityToModel);
    }

    // Helper methods
    private MediaFile entityToModel(MediaFileEntity entity) {
        return MediaFile.builder()
                .mediaId(entity.getMediaId().toString())
                .fileName(entity.getFileName())
                .fileSize(entity.getFileSize())
                .mimeType(entity.getMimeType())
                .url(entity.getUrl())
                .uploaderId(entity.getUploaderId().toString())
                .associatedEntityType(entity.getEntityType())
                .associatedEntityId(entity.getEntityId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

