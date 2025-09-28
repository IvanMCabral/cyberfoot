package com.cyberfoot.domain.ports;

import com.cyberfoot.domain.model.Fixture;
import reactor.core.publisher.Mono;

public interface FixtureRepository {
    Mono<Fixture> findById(String id);
    Mono<Fixture> save(Fixture fixture);
    reactor.core.publisher.Flux<Fixture> findAll();
}
