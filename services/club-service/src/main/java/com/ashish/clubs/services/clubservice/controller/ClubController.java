package com.ashish.clubs.services.clubservice.controller;

import com.ashish.clubs.common.config.AppConstants;
import com.ashish.clubs.common.models.club.Club;
import com.ashish.clubs.common.models.club.Membership;
import com.ashish.clubs.services.clubservice.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(AppConstants.API_V1_PREFIX + "/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    /**
     * Create a new club
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<Club>> createClub(@RequestBody Club club) {
        return clubService.createClub(club)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .onErrorResume(ex -> Mono.just(ResponseEntity.badRequest().build()));
    }

    /**
     * Get all clubs
     */
    @GetMapping
    public Mono<ResponseEntity<Flux<Club>>> getAllClubs() {
        return Mono.just(ResponseEntity.ok(clubService.getAllClubs()));
    }

    /**
     * Get club by ID
     */
    @GetMapping("/{clubId}")
    public Mono<ResponseEntity<Club>> getClubById(@PathVariable UUID clubId) {
        return clubService.getClubById(clubId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Get clubs owned by user
     */
    @GetMapping("/owner/{ownerId}")
    public Mono<ResponseEntity<Flux<Club>>> getClubsByOwner(@PathVariable UUID ownerId) {
        return Mono.just(ResponseEntity.ok(clubService.getClubsByOwner(ownerId)));
    }

    /**
     * Request to join a club
     */
    @PostMapping("/{clubId}/join")
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<Membership>> requestJoinClub(
            @PathVariable UUID clubId,
            @RequestParam UUID userId) {
        return clubService.requestJoinClub(clubId, userId)
                .map(membership -> ResponseEntity.status(HttpStatus.CREATED).body(membership))
                .onErrorResume(ex -> {
                    if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    /**
     * Get club members
     */
    @GetMapping("/{clubId}/members")
    public Mono<ResponseEntity<Flux<Membership>>> getClubMembers(@PathVariable UUID clubId) {
        return Mono.just(ResponseEntity.ok(clubService.getClubMembers(clubId)));
    }

    /**
     * Approve membership request
     */
    @PostMapping("/memberships/{membershipId}/approve")
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<Membership>> approveMembership(@PathVariable UUID membershipId) {
        return clubService.approveMembership(membershipId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(ex -> Mono.just(ResponseEntity.badRequest().build()));
    }
}

