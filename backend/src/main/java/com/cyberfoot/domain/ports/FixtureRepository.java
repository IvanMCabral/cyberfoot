package com.cyberfoot.domain.ports;

import com.cyberfoot.domain.model.Fixture;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface FixtureRepository {
    Mono<Fixture> findById(UUID id);
    Mono<Fixture> save(Fixture fixture);
    reactor.core.publisher.Flux<Fixture> findAll();
}
