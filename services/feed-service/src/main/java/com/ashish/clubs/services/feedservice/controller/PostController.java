package com.ashish.clubs.services.feedservice.controller;

import com.ashish.clubs.common.config.AppConstants;
import com.ashish.clubs.common.models.feed.Post;
import com.ashish.clubs.services.feedservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(AppConstants.API_V1_PREFIX + "/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * Create a new post
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<Post>> createPost(@RequestBody Post post) {
        return postService.createPost(post)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .onErrorResume(ex -> Mono.just(ResponseEntity.badRequest().build()));
    }

    /**
     * Get post by ID
     */
    @GetMapping("/{postId}")
    public Mono<ResponseEntity<Post>> getPostById(@PathVariable String postId) {
        return postService.getPostById(postId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Get posts by author
     */
    @GetMapping("/author/{authorId}")
    public Mono<ResponseEntity<Flux<Post>>> getPostsByAuthor(@PathVariable String authorId) {
        return Mono.just(ResponseEntity.ok(postService.getPostsByAuthor(authorId)));
    }

    /**
     * Get posts by club
     */
    @GetMapping("/club/{clubId}")
    public Mono<ResponseEntity<Flux<Post>>> getPostsByClub(@PathVariable String clubId) {
        return Mono.just(ResponseEntity.ok(postService.getPostsByClub(clubId)));
    }

    /**
     * Get timeline (all published posts with pagination)
     */
    @GetMapping("/timeline")
    public Mono<ResponseEntity<Flux<Post>>> getTimeline(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Mono.just(ResponseEntity.ok(postService.getAllPublishedPosts(page, size)));
    }

    /**
     * Like a post
     */
    @PostMapping("/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<Post>> likePost(
            @PathVariable String postId,
            @RequestParam String userId) {
        return postService.likePost(postId, userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(ex -> Mono.just(ResponseEntity.badRequest().build()));
    }
}

