package com.ashish.clubs.services.clubservice.service;

import com.ashish.clubs.common.config.AppConstants;
import com.ashish.clubs.common.messaging.event.DomainEvent;
import com.ashish.clubs.common.messaging.producer.KafkaEventProducer;
import com.ashish.clubs.common.models.club.Club;
import com.ashish.clubs.common.models.club.Membership;
import com.ashish.clubs.common.models.club.MembershipRole;
import com.ashish.clubs.common.models.club.MembershipStatus;
import com.ashish.clubs.services.clubservice.entity.ClubEntity;
import com.ashish.clubs.services.clubservice.entity.MembershipEntity;
import com.ashish.clubs.services.clubservice.repository.ClubRepository;
import com.ashish.clubs.services.clubservice.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final MembershipRepository membershipRepository;
    private final KafkaEventProducer kafkaEventProducer;

    /**
     * Create a new club
     */
    public Mono<Club> createClub(Club club) {
        log.info("Creating club: {}", club.getName());

        if (club.getClubId() == null) {
            club.setClubId(UUID.randomUUID().toString());
        }

        Instant now = Instant.now();
        club.setCreatedAt(now);
        club.setUpdatedAt(now);
        club.setStatus("ACTIVE");

        ClubEntity entity = entityFromModel(club);

        return clubRepository.save(entity)
                .flatMap(savedEntity -> {
                    Club savedClub = modelFromEntity(savedEntity);

                    // Publish CLUB_CREATED event
                    DomainEvent event = DomainEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("CLUB_CREATED")
                            .sourceService("club-service")
                            .entityId(savedClub.getClubId())
                            .entityType("CLUB")
                            .actorId(savedClub.getOwnerId())
                            .payload(savedClub)
                            .timestamp(Instant.now())
                            .build();

                    return kafkaEventProducer.publishEvent(AppConstants.KAFKA_TOPIC_CLUB_EVENTS, event)
                            .then(Mono.just(savedClub));
                });
    }

    /**
     * Get club by ID
     */
    public Mono<Club> getClubById(UUID clubId) {
        return clubRepository.findByClubId(clubId)
                .map(this::modelFromEntity);
    }

    /**
     * Get all clubs
     */
    public Flux<Club> getAllClubs() {
        return clubRepository.findAll()
                .map(this::modelFromEntity);
    }

    /**
     * Get clubs by owner
     */
    public Flux<Club> getClubsByOwner(UUID ownerId) {
        return clubRepository.findByOwnerId(ownerId)
                .map(this::modelFromEntity);
    }

    /**
     * Request to join a club
     */
    public Mono<Membership> requestJoinClub(UUID clubId, UUID userId) {
        log.info("User {} requesting to join club {}", userId, clubId);

        // Check if club exists
        return clubRepository.findByClubId(clubId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Club not found")))
                .flatMap(club -> membershipRepository.findByClubIdAndUserId(clubId, userId)
                        .hasElement()
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new IllegalArgumentException("Already a member or pending"));
                            }
                            return createNewMembership(clubId, userId);
                        }));
    }

    private Mono<Membership> createNewMembership(UUID clubId, UUID userId) {
        MembershipEntity membership = MembershipEntity.builder()
                .membershipId(UUID.randomUUID())
                .clubId(clubId)
                .userId(userId)
                .role("MEMBER")
                .status(MembershipStatus.PENDING.name())
                .joinDate(Instant.now())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return membershipRepository.save(membership)
                .flatMap(savedMembership -> {
                    Membership model = modelFromMembershipEntity(savedMembership);

                    // Publish event
                    DomainEvent event = DomainEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("CLUB_MEMBERSHIP_REQUESTED")
                            .sourceService("club-service")
                            .entityId(clubId.toString())
                            .entityType("MEMBERSHIP")
                            .actorId(userId.toString())
                            .payload(model)
                            .timestamp(Instant.now())
                            .build();

                    return kafkaEventProducer.publishEvent(AppConstants.KAFKA_TOPIC_CLUB_EVENTS, event)
                            .then(Mono.just(model))
                            .doOnError(ex -> log.error("Failed to publish membership event", ex));
                });
    }

    /**
     * Get club members
     */
    public Flux<Membership> getClubMembers(UUID clubId) {
        return membershipRepository.findByClubIdAndStatus(clubId, "ACTIVE")
                .map(this::modelFromMembershipEntity);
    }

    /**
     * Approve membership request
     */
    public Mono<Membership> approveMembership(UUID membershipId) {
        log.info("Approving membership: {}", membershipId);

        return membershipRepository.findById(membershipId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Membership not found")))
                .flatMap(membership -> {
                    membership.setStatus(MembershipStatus.ACTIVE.name());
                    membership.setJoinDate(Instant.now());
                    membership.setUpdatedAt(Instant.now());

                    return membershipRepository.save(membership)
                            .flatMap(updated -> {
                                Membership model = modelFromMembershipEntity(updated);

                                DomainEvent event = DomainEvent.builder()
                                        .eventId(UUID.randomUUID().toString())
                                        .eventType("CLUB_MEMBERSHIP_APPROVED")
                                        .sourceService("club-service")
                                        .entityId(membership.getClubId().toString())
                                        .entityType("MEMBERSHIP")
                                        .actorId(membership.getUserId().toString())
                                        .payload(model)
                                        .timestamp(Instant.now())
                                        .build();

                                return kafkaEventProducer.publishEvent(AppConstants.KAFKA_TOPIC_CLUB_EVENTS, event)
                                        .then(Mono.just(model));
                            });
                });
    }

    // Helper methods
    private ClubEntity entityFromModel(Club club) {
        return ClubEntity.builder()
                .clubId(UUID.fromString(club.getClubId()))
                .name(club.getName())
                .description(club.getDescription())
                .ownerId(UUID.fromString(club.getOwnerId()))
                .imageUrl(club.getImageUrl())
                .location(club.getLocation())
                .status(club.getStatus())
                .createdAt(club.getCreatedAt())
                .updatedAt(club.getUpdatedAt())
                .build();
    }

    private Club modelFromEntity(ClubEntity entity) {
        return Club.builder()
                .clubId(entity.getClubId().toString())
                .name(entity.getName())
                .description(entity.getDescription())
                .ownerId(entity.getOwnerId().toString())
                .imageUrl(entity.getImageUrl())
                .location(entity.getLocation())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private Membership modelFromMembershipEntity(MembershipEntity entity) {
        return Membership.builder()
                .membershipId(entity.getMembershipId().toString())
                .clubId(entity.getClubId().toString())
                .userId(entity.getUserId().toString())
                .role(MembershipRole.valueOf(entity.getRole()))
                .status(MembershipStatus.valueOf(entity.getStatus()))
                .joinDate(entity.getJoinDate())
                .endDate(entity.getEndDate())
                .build();
    }
}
