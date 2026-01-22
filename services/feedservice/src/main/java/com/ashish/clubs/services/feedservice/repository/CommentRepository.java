package com.ashish.clubs.services.feedservice.repository;

import com.ashish.clubs.services.feedservice.entity.CommentDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CommentRepository extends ReactiveMongoRepository<CommentDocument, String> {
    Mono<CommentDocument> findByCommentId(String commentId);
    Flux<CommentDocument> findByPostId(String postId);
    Flux<CommentDocument> findByAuthorId(String authorId);
    Flux<CommentDocument> findByStatus(String status);
}

