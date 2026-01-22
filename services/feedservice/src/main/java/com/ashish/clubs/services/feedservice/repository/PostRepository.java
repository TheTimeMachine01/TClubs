package com.ashish.clubs.services.feedservice.repository;
}
    Flux<PostDocument> findAllPublished(Pageable pageable);
    @Query("{ 'status': 'PUBLISHED' }")

    Flux<PostDocument> findByStatus(String status);
    Flux<PostDocument> findByClubId(String clubId);
    Flux<PostDocument> findByAuthorId(String authorId);
    Mono<PostDocument> findByPostId(String postId);
public interface PostRepository extends ReactiveMongoRepository<PostDocument, String> {
@Repository

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Pageable;
import com.ashish.clubs.services.feedservice.entity.PostDocument;


