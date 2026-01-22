package com.ashish.clubs.services.feedservice.service;

import com.ashish.clubs.common.config.AppConstants;
import com.ashish.clubs.common.messaging.event.DomainEvent;
import com.ashish.clubs.common.messaging.producer.KafkaEventProducer;
import com.ashish.clubs.common.models.feed.Post;
import com.ashish.clubs.services.feedservice.entity.PostDocument;
import com.ashish.clubs.services.feedservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final KafkaEventProducer kafkaEventProducer;

    /**
     * Create a new post
     */
    public Mono<Post> createPost(Post post) {
        log.info("Creating post by author: {}", post.getAuthorId());

        if (post.getPostId() == null) {
            post.setPostId(UUID.randomUUID().toString());
        }

        Instant now = Instant.now();
        post.setTimestamp(now);

        PostDocument document = modelToDocument(post);
        document.setCreatedAt(now);
        document.setUpdatedAt(now);
        document.setStatus("PUBLISHED");
        document.setLikeCount(0);
        document.setCommentCount(0);

        return postRepository.save(document)
                .flatMap(savedDoc -> {
                    Post savedPost = documentToModel(savedDoc);

                    // Publish POST_CREATED event
                    DomainEvent event = DomainEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("POST_CREATED")
                            .sourceService("feed-service")
                            .entityId(savedPost.getPostId())
                            .entityType("POST")
                            .actorId(savedPost.getAuthorId())
                            .payload(savedPost)
                            .timestamp(Instant.now())
                            .build();

                    return kafkaEventProducer.publishEvent(AppConstants.KAFKA_TOPIC_FEED_EVENTS, event)
                            .thenReturn(savedPost)
                            .doOnError(ex -> log.error("Failed to publish post creation event", ex));
                });
    }

    /**
     * Get post by ID
     */
    public Mono<Post> getPostById(String postId) {
        return postRepository.findByPostId(postId)
                .map(this::documentToModel);
    }

    /**
     * Get posts by author
     */
    public Flux<Post> getPostsByAuthor(String userId) {
        return postRepository.findByUserId(userId)
                .map(this::documentToModel);
    }

    /**
     * Get posts by club
     */
    public Flux<Post> getPostsByClub(String clubId) {
        return postRepository.findByClubId(clubId)
                .map(this::documentToModel);
    }

    /**
     * Get all published posts with pagination
     */
    public Flux<Post> getAllPublishedPosts(int page, int size) {
        return postRepository.findAllBy(PageRequest.of(page, size))
                .map(this::documentToModel);
    }

    /**
     * Like a post
     */
    public Mono<Post> likePost(String postId, String userId) {
        log.info("User {} liked post {}", userId, postId);

        return postRepository.findByPostId(postId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Post not found")))
                .flatMap(post -> {
                    post.setLikeCount(post.getLikeCount() + 1);
                    post.setUpdatedAt(Instant.now());

                    return postRepository.save(post)
                            .flatMap(updated -> {
                                Post model = documentToModel(updated);

                                // Publish POST_LIKED event
                                DomainEvent event = DomainEvent.builder()
                                        .eventId(UUID.randomUUID().toString())
                                        .eventType("POST_LIKED")
                                        .sourceService("feed-service")
                                        .entityId(postId)
                                        .entityType("POST")
                                        .actorId(userId)
                                        .payload(model)
                                        .timestamp(Instant.now())
                                        .build();

                                return kafkaEventProducer.publishEvent(AppConstants.KAFKA_TOPIC_FEED_EVENTS, event)
                                        .thenReturn(model)
                                        .doOnError(ex -> log.error("Failed to publish post like event", ex));
                            });
                });
    }

    // Helper methods

    private PostDocument modelToDocument(Post post) {
        return PostDocument.builder()
                .postId(UUID.fromString(post.getPostId()))
                .content(post.getContent())
                .userId(post.getAuthorId())
                .mediaUrls(post.getMediaUrls())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .build();
    }

    private Post documentToModel(PostDocument doc) {
        return Post.builder()
                .postId(doc.getPostId().toString())
                .content(doc.getContent())
                .authorId(doc.getUserId())
                .timestamp(doc.getCreatedAt())
                .mediaUrls(doc.getMediaUrls())
                .likeCount((int) doc.getLikeCount())
                .commentCount((int) doc.getCommentCount())
                .build();
    }
}

