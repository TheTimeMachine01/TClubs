package com.ashish.clubs.services.clubservice.repository;

import com.ashish.clubs.services.clubservice.entity.ClubEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public interface ClubRepository extends R2dbcRepository<ClubEntity, UUID> {

    Mono<ClubEntity> findByClubId(UUID clubId);

    Flux<ClubEntity> findByOwnerId(UUID ownerId);

    Flux<ClubEntity> findByStatus(String status);

    @Query("SELECT * FROM clubs WHERE status = :status LIMIT :limit OFFSET :offset")
    Flux<ClubEntity> findByStatusWithPagination(String status, int limit, int offset);
}

