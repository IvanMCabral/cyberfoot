
package com.cyberfoot.adapters.persistence.season;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonMongoRepository extends ReactiveMongoRepository<SeasonEntity, String> {
}
