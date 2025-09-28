package com.cyberfoot.adapters.persistence.club;

import com.cyberfoot.domain.model.Club;
import com.cyberfoot.domain.ports.ClubRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Component
public class ClubMongoAdapter implements ClubRepository {
    private final ClubMongoRepository mongoRepo;

    public ClubMongoAdapter(ClubMongoRepository mongoRepo) {
        this.mongoRepo = mongoRepo;
    }

    @Override
    public Mono<Club> findById(String id) {
        return mongoRepo.findById(id)
            .map(ClubMapper::toDomain);
    }

    @Override
    public Mono<Club> save(Club club) {
        return mongoRepo.save(ClubMapper.toEntity(club))
            .map(ClubMapper::toDomain);
    }

    @Override
    public Flux<Club> findAll() {
        return mongoRepo.findAll()
            .map(ClubMapper::toDomain);
    }
}
