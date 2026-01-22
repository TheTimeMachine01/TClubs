package com.ashish.clubs.services.mediaservice.controller;

import com.ashish.clubs.common.config.AppConstants;
import com.ashish.clubs.common.models.media.MediaFile;
import com.ashish.clubs.services.mediaservice.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(AppConstants.API_V1_PREFIX + "/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    /**
     * Upload a file
     */
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<MediaFile>> uploadFile(
            @RequestPart("file") FilePart filePart,
            @RequestParam String uploaderId,
            @RequestParam String entityType,
            @RequestParam(required = false) String entityId) {

        String finalEntityId = entityId != null ? entityId : UUID.randomUUID().toString();

        return mediaService.uploadFile(filePart, uploaderId, entityType, finalEntityId)
                .map(mediaFile -> ResponseEntity.status(HttpStatus.CREATED).body(mediaFile))
                .onErrorResume(ex -> {
                    log.error("Error uploading file", ex);
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    /**
     * Get media file by ID
     */
    @GetMapping("/{mediaId}")
    public Mono<ResponseEntity<MediaFile>> getMediaFile(@PathVariable UUID mediaId) {
        return mediaService.getMediaFileById(mediaId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MediaController.class);
}

