package com.cyberfoot.adapters.persistence.club;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import java.util.UUID;

public interface ClubRepositoryR2dbc extends ReactiveCrudRepository<ClubEntity, UUID> {
}