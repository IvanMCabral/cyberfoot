package com.cyberfoot.adapters.web;
import java.util.Map;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import com.cyberfoot.domain.model.MatchEvent;
import com.cyberfoot.domain.ports.SimulateMatchUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@RestController
@Validated
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class MatchController {
    // Endpoint temporal para debug: loguear el raw body recibido
    @PostMapping("/match/{fixtureId}/debugraw")
    public Mono<Void> debugRawBody(@PathVariable("fixtureId") String fixtureId, @RequestBody String rawBody) {
        logger.info("[DEBUGRAW] POST /match/{}/debugraw recibido. Raw body: {}", fixtureId, rawBody);
        return Mono.empty();
    }
    // Variables para guardar la última alineación enviada por POST
    private final java.util.Map<String, LineupRequest> lastLineups = new java.util.HashMap<>();
    private static final Logger logger = LogManager.getLogger(MatchController.class);
    private final SimulateMatchUseCase matchUseCase;

    public MatchController(SimulateMatchUseCase matchUseCase) {
        this.matchUseCase = matchUseCase;
    }

    // El endpoint GET de eventos debe ser adaptado para recibir titulares, o eliminarse si no se usa

    @PostMapping("/match/{fixtureId}/simulate")
    public Mono<Void> triggerSimulation(@PathVariable("fixtureId") String fixtureId, @RequestBody Map<String, Object> body) {
        logger.info("[PLAY] POST /match/{}/simulate recibido. Raw body: {}", fixtureId, body);
        String homeClubId = null;
        String awayClubId = null;
        java.util.List<Integer> homeRatings = null;
        java.util.List<Integer> awayRatings = null;

        if (body.containsKey("homeClubId") && body.containsKey("awayClubId")) {
            homeClubId = (String) body.get("homeClubId");
            awayClubId = (String) body.get("awayClubId");
            homeRatings = (java.util.List<Integer>) body.get("homeRatings");
            awayRatings = (java.util.List<Integer>) body.get("awayRatings");
            logger.info("[PLAY] Body recibido: homeClubId={}, awayClubId={}, homeRatings={}, awayRatings={}",
                homeClubId, awayClubId, homeRatings, awayRatings);
        } else if (body.containsKey("teamId") && body.containsKey("starters")) {
            homeClubId = (String) body.get("teamId");
            java.util.List<String> starters = (java.util.List<String>) body.get("starters");
            logger.info("[PLAY] Body recibido: teamId={}, starters={}", homeClubId, starters);
            homeRatings = null;
            awayClubId = null;
            awayRatings = null;
        } else {
            logger.warn("[PLAY] Body recibido en formato desconocido: {}", body);
        }
        LineupRequest lineup = new LineupRequest();
        lineup.setHomeClubId(homeClubId);
        lineup.setAwayClubId(awayClubId);
        lineup.setHomeRatings(homeRatings);
        lineup.setAwayRatings(awayRatings);
        lastLineups.put(fixtureId, lineup);

        // Ensure fixture exists before simulation
        // Use fixtureRepo to check and create if missing
    com.cyberfoot.domain.ports.FixtureRepository fixtureRepo = ((com.cyberfoot.app.usecase.SimulateMatchService) matchUseCase).getFixtureRepo();
        return fixtureRepo.findById(fixtureId)
            .switchIfEmpty(
                // If not found, create a new fixture with provided data
                fixtureRepo.save(new com.cyberfoot.domain.model.Fixture(
                    fixtureId,
                    homeClubId,
                    awayClubId,
                    java.time.Instant.now(), // scheduledAt
                    "SCHEDULED", // status
                    0, // goalsHome
                    0, // goalsAway
                    "season1", // seasonId (adjust as needed)
                    1 // matchday (adjust as needed)
                ))
            )
            .then(
                matchUseCase.simulate(fixtureId, homeRatings, awayRatings)
                    .take(1)
                    .then()
            );
    }
    @GetMapping(value = "/match/{fixtureId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<com.cyberfoot.domain.model.MatchEvent>> streamMatch(@PathVariable("fixtureId") String fixtureId) {
    LineupRequest lineup = lastLineups.get(fixtureId);
    java.util.List<Integer> homeRatings = lineup != null ? lineup.getHomeRatings() : null;
    java.util.List<Integer> awayRatings = lineup != null ? lineup.getAwayRatings() : null;
    double homeOverall = homeRatings != null && !homeRatings.isEmpty() ? homeRatings.stream().mapToInt(Integer::intValue).average().orElse(0) : 0;
    double awayOverall = awayRatings != null && !awayRatings.isEmpty() ? awayRatings.stream().mapToInt(Integer::intValue).average().orElse(0) : 0;
    logger.info("[MATCHUP] Fixture {}: Home overall (titulares) = {} | Away overall (titulares) = {}", fixtureId, homeOverall, awayOverall);
    // Only stream events, do not persist again
    return matchUseCase.simulate(fixtureId, homeRatings, awayRatings)
        .doOnSubscribe(sub -> logger.info("[PLAY] Suscripci3n SSE iniciada para fixtureId: {}", fixtureId))
        .doOnNext(event -> logger.info("[PLAY] Evento SSE: {}", event))
        .doOnError(err -> logger.error("[PLAY] Error en simulaci3n SSE: {}", err.getMessage(), err))
        .doOnComplete(() -> logger.info("[PLAY] Simulaci3n SSE completada para fixtureId: {}", fixtureId))
        .map(event -> ServerSentEvent.builder(event).build());
    }

    // ...existing code...
}
