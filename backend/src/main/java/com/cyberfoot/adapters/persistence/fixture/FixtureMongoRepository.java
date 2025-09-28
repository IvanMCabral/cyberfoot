package com.cyberfoot.adapters.persistence.fixture;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
public interface FixtureMongoRepository extends ReactiveMongoRepository<FixtureEntity, String> {
}