package com.cyberfoot.adapters.persistence.fixture;

import com.cyberfoot.domain.model.Fixture;
import com.cyberfoot.domain.ports.FixtureRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Component
public class FixtureMongoAdapter implements FixtureRepository {
    private final FixtureMongoRepository mongoRepo;

    public FixtureMongoAdapter(FixtureMongoRepository mongoRepo) {
        this.mongoRepo = mongoRepo;
    }

    @Override
    public Mono<Fixture> findById(String id) {
        return mongoRepo.findById(id)
            .map(FixtureMapper::toDomain);
    }

    @Override
    public Mono<Fixture> save(Fixture fixture) {
        return mongoRepo.save(FixtureMapper.toEntity(fixture))
            .map(FixtureMapper::toDomain);
    }

    @Override
    public Flux<Fixture> findAll() {
        return mongoRepo.findAll()
            .map(FixtureMapper::toDomain);
    }
}
