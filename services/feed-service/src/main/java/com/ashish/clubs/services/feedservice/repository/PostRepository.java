package com.ashish.clubs.services.feedservice.repository;

import com.ashish.clubs.services.feedservice.entity.PostDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PostRepository extends ReactiveMongoRepository<PostDocument, UUID> {
    Flux<PostDocument> findByClubId(String clubId);
    Flux<PostDocument> findByUserId(String userId);
    Mono<PostDocument> findByPostId(String postId);
    Flux<PostDocument> findAllBy(Pageable pageable);
}

