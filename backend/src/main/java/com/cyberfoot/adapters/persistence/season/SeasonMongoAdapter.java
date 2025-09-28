package com.cyberfoot.adapters.persistence.season;

import com.cyberfoot.domain.model.Season;
import com.cyberfoot.domain.ports.SeasonRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
// import java.util.UUID;

@Component
public class SeasonMongoAdapter implements SeasonRepository {
    private final SeasonMongoRepository mongoRepo;

    public SeasonMongoAdapter(SeasonMongoRepository mongoRepo) {
        this.mongoRepo = mongoRepo;
    }

    @Override
    public Mono<Season> findById(String id) {
        return mongoRepo.findById(id)
            .map(SeasonMapper::toDomain);
    }

    @Override
    public Mono<Season> findActive() {
        return mongoRepo.findAll()
            .filter(SeasonEntity::isActive)
            .next()
            .map(SeasonMapper::toDomain);
    }
}
