package com.cyberfoot.app.usecase;

import com.cyberfoot.domain.model.Fixture;
import com.cyberfoot.domain.model.Club;
import com.cyberfoot.domain.model.MatchEvent;
import com.cyberfoot.domain.ports.FixtureRepository;
import com.cyberfoot.domain.ports.ClubRepository;
import com.cyberfoot.domain.ports.SimulateMatchUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

@Service
public class SimulateMatchService implements SimulateMatchUseCase {
    private final FixtureRepository fixtureRepo;
    private final ClubRepository clubRepo;
    private final Random random;

    public SimulateMatchService(FixtureRepository fixtureRepo, ClubRepository clubRepo,
                               @Value("${engine.seed:42}") long seed) {
        this.fixtureRepo = fixtureRepo;
        this.clubRepo = clubRepo;
        this.random = new Random(seed);
    }

    @Override
    public Flux<MatchEvent> simulate(UUID fixtureId) {
        return fixtureRepo.findById(fixtureId)
            .switchIfEmpty(Mono.error(new RuntimeException("Fixture not found")))
            .flatMapMany(fixture -> {
                // Set status LIVE and persist
                Fixture liveFixture = new Fixture(
                    fixture.id(), fixture.homeClubId(), fixture.awayClubId(), fixture.scheduledAt(),
                    "LIVE", fixture.goalsHome(), fixture.goalsAway()
                );
                return fixtureRepo.save(liveFixture)
                    .thenMany(simulateMatch(liveFixture));
            });
    }

    private Flux<MatchEvent> simulateMatch(Fixture fixture) {
        Mono<Club> homeMono = clubRepo.findById(fixture.homeClubId());
        Mono<Club> awayMono = clubRepo.findById(fixture.awayClubId());
        return Mono.zip(homeMono, awayMono)
            .flatMapMany(tuple -> {
                Club home = tuple.getT1();
                Club away = tuple.getT2();
                double H = home != null ? home.overall() : 70;
                double A = away != null ? away.overall() : 70;
                double pHome = (H / (H + A)) * 0.08;
                double pAway = (A / (H + A)) * 0.08;
                class State {
                    int goalsHome = 0;
                    int goalsAway = 0;
                    int seq = 0;
                }
                State state = new State();
                Flux<MatchEvent> ticks = Flux.range(1, 90)
                    .delayElements(Duration.ofMillis(55)) // ~5s total
                    .map(minute -> {
                        double r = random.nextDouble();
                        String type = "TICK";
                        String msg = "Minuto " + minute;
                        if (r < pHome) {
                            state.goalsHome++;
                            type = "GOAL";
                            msg = "Gol local! " + state.goalsHome + "-" + state.goalsAway;
                        } else if (r < pHome + pAway) {
                            state.goalsAway++;
                            type = "GOAL";
                            msg = "Gol visitante! " + state.goalsHome + "-" + state.goalsAway;
                        }
                        return new MatchEvent(++state.seq, minute, type, state.goalsHome, state.goalsAway, msg);
                    });
                return ticks.concatWith(
                    Mono.defer(() -> {
                        // Persistir resultado final y status FT
                        Fixture ftFixture = new Fixture(
                            fixture.id(), fixture.homeClubId(), fixture.awayClubId(), fixture.scheduledAt(),
                            "FT", state.goalsHome, state.goalsAway
                        );
                        return fixtureRepo.save(ftFixture)
                            .thenReturn(new MatchEvent(++state.seq, 90, "END", state.goalsHome, state.goalsAway, "Finalizado"));
                    })
                );
            });
    }
    // TODO[M1.1]: Lesiones, tarjetas, táctica, fitness
}
