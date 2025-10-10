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
@Service
public class SimulateMatchService implements SimulateMatchUseCase {
    private final FixtureRepository fixtureRepo;

    public FixtureRepository getFixtureRepo() {
        return fixtureRepo;
    }
    private final ClubRepository clubRepo;
    private final Random random;

    public SimulateMatchService(FixtureRepository fixtureRepo, ClubRepository clubRepo,
                               @Value("${engine.seed:42}") long seed) {
        this.fixtureRepo = fixtureRepo;
        this.clubRepo = clubRepo;
        this.random = new Random(seed);
    }

    @Override
    public Flux<MatchEvent> simulate(String fixtureId, java.util.List<Integer> homeRatings, java.util.List<Integer> awayRatings) {
        return fixtureRepo.findById(fixtureId)
            .switchIfEmpty(Mono.error(new RuntimeException("Fixture not found")))
            .flatMapMany(fixture -> {
                Fixture liveFixture = new Fixture(
                    fixture.getId(), fixture.getHomeClubId(), fixture.getAwayClubId(), fixture.getScheduledAt(),
                    "LIVE", fixture.getGoalsHome(), fixture.getGoalsAway(), fixture.getSeasonId(), fixture.getMatchday()
                );
                return fixtureRepo.save(liveFixture)
                    .thenMany(this.simulateMatch(liveFixture, homeRatings, awayRatings));
            });
    }

    private Flux<MatchEvent> simulateMatch(Fixture fixture, java.util.List<Integer> homeRatings, java.util.List<Integer> awayRatings) {
        double H = homeRatings != null && !homeRatings.isEmpty() ? homeRatings.stream().mapToInt(Integer::intValue).average().orElse(70) : 70;
        double A = awayRatings != null && !awayRatings.isEmpty() ? awayRatings.stream().mapToInt(Integer::intValue).average().orElse(70) : 70;
        double exp = 2.0;
        double multiplier = 0.06;
        final double pHome = (Math.pow(H, exp) / (Math.pow(H, exp) + Math.pow(A, exp))) * multiplier;
        final double pAway = (Math.pow(A, exp) / (Math.pow(H, exp) + Math.pow(A, exp))) * multiplier;
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
                    fixture.getId(), fixture.getHomeClubId(), fixture.getAwayClubId(), fixture.getScheduledAt(),
                    "FT", state.goalsHome, state.goalsAway, fixture.getSeasonId(), fixture.getMatchday()
                );
                return fixtureRepo.save(ftFixture)
                    .thenReturn(new MatchEvent(++state.seq, 90, "END", state.goalsHome, state.goalsAway, "Finalizado"));
            })
        );
    }
    // Simulación realista usando ratings
    private int simulateGoals(int homeOverall, int awayOverall, boolean isHome) {
        double exp = 2.0;
        double multiplier = 0.06;
        double H = homeOverall;
        double A = awayOverall;
        double pHome = (Math.pow(H, exp) / (Math.pow(H, exp) + Math.pow(A, exp))) * multiplier;
        double pAway = (Math.pow(A, exp) / (Math.pow(H, exp) + Math.pow(A, exp))) * multiplier;
        double p = isHome ? pHome : pAway;
        int goals = 0;
        Random rand = new Random();
        for (int i = 0; i < 90; i++) {
            if (rand.nextDouble() < p) goals++;
        }
        return goals;
    }
    // Simulación batch de partidos (sin eventos minuto a minuto, solo resultado final)
    public Flux<Fixture> batchSimulate(java.util.List<Fixture> fixtures, java.util.Map<String, Club> clubMap) {
        return Flux.fromIterable(fixtures)
            .flatMap(fixture -> {
                Club home = clubMap.get(fixture.getHomeClubId());
                Club away = clubMap.get(fixture.getAwayClubId());
                int homeOverall = home != null ? home.overall() : 70;
                int awayOverall = away != null ? away.overall() : 70;
                int goalsHome = simulateGoals(homeOverall, awayOverall, true);
                int goalsAway = simulateGoals(homeOverall, awayOverall, false);
                Fixture finished = new Fixture(
                    fixture.getId(), fixture.getHomeClubId(), fixture.getAwayClubId(), fixture.getScheduledAt(),
                    "FINISHED", goalsHome, goalsAway, fixture.getSeasonId(), fixture.getMatchday()
                );
                return fixtureRepo.save(finished);
            });
    }
}
