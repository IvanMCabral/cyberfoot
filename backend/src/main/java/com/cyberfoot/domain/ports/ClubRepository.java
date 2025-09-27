package com.cyberfoot.domain.ports;

import com.cyberfoot.domain.model.Club;
import reactor.core.publisher.Mono;
import java.util.UUID;

import reactor.core.publisher.Flux;

public interface ClubRepository {
    Mono<Club> findById(UUID id);
    Mono<Club> save(Club club);
    Flux<Club> findAll();
}
