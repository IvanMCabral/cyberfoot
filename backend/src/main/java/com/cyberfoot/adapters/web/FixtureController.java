package com.cyberfoot.adapters.web;

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

    public FixtureController(FixtureRepository fixtureRepo, ClubRepository clubRepo) {
        this.fixtureRepo = fixtureRepo;
        this.clubRepo = clubRepo;
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
            // Algoritmo round-robin
            List<Club> clubList = new ArrayList<>(clubs);
            if (n % 2 != 0) clubList.add(null); // Si impar, agregar dummy
            int numRounds = clubList.size() - 1;
            int numMatchesPerRound = clubList.size() / 2;
            for (int round = 0; round < numRounds; round++) {
                List<Map<String, Object>> matches = new ArrayList<>();
                for (int match = 0; match < numMatchesPerRound; match++) {
                    Club home = clubList.get(match);
                    Club away = clubList.get(clubList.size() - 1 - match);
                    if (home != null && away != null) {
                        Fixture f = new Fixture(
                            UUID.randomUUID().toString(),
                            home.id(),
                            away.id(),
                            null,
                            "SCHEDULED",
                            0,
                            0,
                            null,
                            round + 1
                        );
                        fixtureRepo.save(f).subscribe();
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id", f.id());
                        dto.put("homeClubId", f.homeClubId());
                        dto.put("homeClubName", home.name());
                        dto.put("awayClubId", f.awayClubId());
                        dto.put("awayClubName", away.name());
                        dto.put("matchday", round + 1);
                        matches.add(dto);
                    }
                }
                // Rotar los equipos para la siguiente ronda
                if (clubList.size() > 2) {
                    Club last = clubList.remove(clubList.size() - 1);
                    clubList.add(1, last);
                }
                rounds.add(matches);
            }
            Map<String, Object> response = new HashMap<>();
            response.put("rounds", rounds);
            return Mono.just(response);
        });
    }
    }
