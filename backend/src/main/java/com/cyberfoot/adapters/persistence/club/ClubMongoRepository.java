
package com.cyberfoot.adapters.persistence.club;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubMongoRepository extends ReactiveMongoRepository<ClubEntity, String> {
}
