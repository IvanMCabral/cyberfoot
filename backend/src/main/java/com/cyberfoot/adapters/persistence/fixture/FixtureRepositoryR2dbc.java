package com.cyberfoot.adapters.persistence.fixture;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import java.util.UUID;

public interface FixtureRepositoryR2dbc extends ReactiveCrudRepository<FixtureEntity, UUID> {
}
