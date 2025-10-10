package com.cyberfoot.adapters.persistence.gamesession;

import com.cyberfoot.domain.model.GameSession;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSessionRepository extends ReactiveMongoRepository<GameSession, String> {
    // Métodos CRUD ya incluidos por MongoRepository
    reactor.core.publisher.Mono<Void> deleteByLastActiveBefore(java.time.Instant threshold);
}
