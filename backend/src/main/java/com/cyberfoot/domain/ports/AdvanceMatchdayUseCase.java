package com.cyberfoot.domain.ports;

import com.cyberfoot.domain.model.BatchEvent;
import reactor.core.publisher.Flux;
import java.util.UUID;

public interface AdvanceMatchdayUseCase {
    Flux<BatchEvent> advance(UUID seasonId);
}
