package com.ashish.clubs.services.feedservice.repository;

import com.ashish.clubs.services.feedservice.entity.LikeDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LikeRepository extends ReactiveMongoRepository<LikeDocument, String> {
    Mono<LikeDocument> findByEntityIdAndUserId(String entityId, String userId);
    Flux<LikeDocument> findByEntityId(String entityId);
    Flux<LikeDocument> findByUserId(String userId);
    Mono<Long> countByEntityId(String entityId);
}

