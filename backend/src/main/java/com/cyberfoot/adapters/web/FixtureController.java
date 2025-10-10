package com.cyberfoot.adapters.web;

import java.util.Random;

import com.cyberfoot.domain.model.Fixture;
import com.cyberfoot.domain.ports.FixtureRepository;
import com.cyberfoot.domain.ports.ClubRepository;
import com.cyberfoot.domain.model.Club;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class FixtureController {
    private final FixtureRepository fixtureRepo;
    private final ClubRepository clubRepo;
    // private final com.cyberfoot.app.usecase.SimulateMatchService simulateMatchService; // No usado en batch

    public FixtureController(FixtureRepository fixtureRepo, ClubRepository clubRepo, com.cyberfoot.app.usecase.SimulateMatchService simulateMatchService) {
        this.fixtureRepo = fixtureRepo;
        this.clubRepo = clubRepo;
        // this.simulateMatchService = simulateMatchService;
    }

    @GetMapping("/fixtures/{id}")
    public Mono<Fixture> getFixture(@PathVariable String id) {
        return fixtureRepo.findById(id);
    }

    @GetMapping("/fixtures")
    public Flux<Fixture> getAllFixtures() {
        return fixtureRepo.findAll();
    }

    @PostMapping("/fixtures")
    public Mono<Object> createFullFixture() {
        return clubRepo.findAll().collectList().flatMap(clubs -> {
            int n = clubs.size();
            List<List<Map<String, Object>>> rounds = new ArrayList<>();
            List<Club> clubList = new ArrayList<>(clubs);
            if (n % 2 != 0) clubList.add(null); // Si impar, agregar dummy
            int numRounds = clubList.size() - 1;
            int numMatchesPerRound = clubList.size() / 2;
            for (int round = 0; round < numRounds; round++) {
                List<Map<String, Object>> matches = new ArrayList<>();
                List<Mono<Void>> saves = new ArrayList<>();
                for (int match = 0; match < numMatchesPerRound; match++) {
                    Club home = clubList.get(match);
                    Club away = clubList.get(clubList.size() - 1 - match);
                    if (home != null && away != null) {
                        // Usar overall para simular resultado realista
                        Fixture f = new Fixture(
                            UUID.randomUUID().toString(),
                            home.id(),
                            away.id(),
                            null,
                            "SCHEDULED",
                            null,
                            null,
                            null,
                            round + 1
                        );
                        saves.add(fixtureRepo.save(f).then());
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id", f.getId());
                        dto.put("homeClubId", f.getHomeClubId());
                        dto.put("homeClubName", home.name());
                        dto.put("awayClubId", f.getAwayClubId());
                        dto.put("awayClubName", away.name());
                        dto.put("matchday", round + 1);
                        // No incluir matchResult al crear el fixture
                        matches.add(dto);
                    }
                }
                // Rotar los equipos para la siguiente ronda
                if (clubList.size() > 2) {
                    Club last = clubList.remove(clubList.size() - 1);
                    clubList.add(1, last);
                }
                rounds.add(matches);
                // Guardar todos los partidos de la ronda en paralelo
                Mono.when(saves).subscribe();
            }
            Map<String, Object> response = new HashMap<>();
            response.put("rounds", rounds);
            return Mono.just(response);
        });
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
    }
