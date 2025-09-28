package com.cyberfoot.adapters.persistence.player;

import com.cyberfoot.domain.model.Player;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface PlayerMongoRepository extends ReactiveMongoRepository<Player, String> {
    Flux<Player> findByClubId(String clubId);
}
