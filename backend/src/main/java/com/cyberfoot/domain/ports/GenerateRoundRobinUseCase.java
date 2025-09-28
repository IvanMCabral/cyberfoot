package com.cyberfoot.domain.ports;

import reactor.core.publisher.Mono;
import java.util.UUID;

public interface GenerateRoundRobinUseCase {
    Mono<Integer> generate(UUID seasonId);
}
