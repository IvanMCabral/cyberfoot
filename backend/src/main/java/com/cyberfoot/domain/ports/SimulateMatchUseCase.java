package com.cyberfoot.domain.ports;

import com.cyberfoot.domain.model.MatchEvent;
import reactor.core.publisher.Flux;
import java.util.UUID;

public interface SimulateMatchUseCase {
    Flux<MatchEvent> simulate(UUID fixtureId);
}
