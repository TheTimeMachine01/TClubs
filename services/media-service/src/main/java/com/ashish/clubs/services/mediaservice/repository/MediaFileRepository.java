package com.ashish.clubs.services.mediaservice.repository;

import com.ashish.clubs.services.mediaservice.entity.MediaFileEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface MediaFileRepository extends R2dbcRepository<MediaFileEntity, UUID> {
    Mono<MediaFileEntity> findByMediaId(UUID mediaId);
    Flux<MediaFileEntity> findByUploaderId(UUID uploaderId);
    Flux<MediaFileEntity> findByEntityId(String entityId);
    Flux<MediaFileEntity> findByEntityType(String entityType);
    Mono<MediaFileEntity> findByMinioPath(String minioPath);
}

