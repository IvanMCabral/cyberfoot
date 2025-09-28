package com.cyberfoot.domain.ports;

import com.cyberfoot.domain.model.Season;
import reactor.core.publisher.Mono;
// import java.util.UUID;

public interface SeasonRepository {
    Mono<Season> findById(String id);
    Mono<Season> findActive();
}
