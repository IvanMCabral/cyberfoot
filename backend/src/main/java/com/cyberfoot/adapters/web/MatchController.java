package com.cyberfoot.adapters.web;

import com.cyberfoot.domain.model.MatchEvent;
import com.cyberfoot.domain.model.Fixture;
import com.cyberfoot.domain.ports.SimulateMatchUseCase;
import com.cyberfoot.domain.ports.FixtureRepository;
import com.cyberfoot.adapters.persistence.fixture.FixtureRepositoryAdapter;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class MatchController {
    private static final Logger logger = LogManager.getLogger(MatchController.class);
    private final SimulateMatchUseCase matchUseCase;
    private final FixtureRepository fixtureRepo;

    public MatchController(SimulateMatchUseCase matchUseCase, FixtureRepository fixtureRepo) {
        this.matchUseCase = matchUseCase;
        this.fixtureRepo = fixtureRepo;
    }

    @GetMapping(value = "/match/{fixtureId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<MatchEvent>> streamMatch(@PathVariable UUID fixtureId) {
        logger.info("[PLAY] Simulación solicitada para fixtureId: {}", fixtureId);
        return matchUseCase.simulate(fixtureId)
            .doOnSubscribe(sub -> logger.info("[PLAY] Suscripción SSE iniciada para fixtureId: {}", fixtureId))
            .doOnNext(event -> logger.info("[PLAY] Evento SSE: {}", event))
            .doOnError(err -> logger.error("[PLAY] Error en simulación SSE: {}", err.getMessage(), err))
            .doOnComplete(() -> logger.info("[PLAY] Simulación SSE completada para fixtureId: {}", fixtureId))
            .map(event -> ServerSentEvent.builder(event).build());
    }


}
