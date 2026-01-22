package com.ashish.clubs.services.clubservice.repository;

import com.ashish.clubs.services.clubservice.entity.MembershipEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface MembershipRepository extends R2dbcRepository<MembershipEntity, UUID> {

    Mono<MembershipEntity> findByMembershipId(UUID membershipId);

    Flux<MembershipEntity> findByClubId(UUID clubId);

    Flux<MembershipEntity> findByUserId(UUID userId);

    Mono<MembershipEntity> findByClubIdAndUserId(UUID clubId, UUID userId);

    Flux<MembershipEntity> findByClubIdAndStatus(UUID clubId, String status);

    @Query("SELECT * FROM memberships WHERE club_id = :clubId AND status = :status LIMIT :limit OFFSET :offset")
    Flux<MembershipEntity> findByClubIdAndStatusWithPagination(UUID clubId, String status, int limit, int offset);
}

