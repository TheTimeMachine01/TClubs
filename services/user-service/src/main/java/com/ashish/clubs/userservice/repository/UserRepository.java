package com.ashish.clubs.userservice.repository;

import com.ashish.clubs.userservice.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByEmail(String username); // Example custom query

    Mono<User> findByUserId(String userId);
}
