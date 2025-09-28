package com.cyberfoot.domain.ports;

import com.cyberfoot.domain.model.MatchEvent;
import reactor.core.publisher.Flux;

public interface SimulateMatchUseCase {
    Flux<MatchEvent> simulate(String fixtureId);
}
