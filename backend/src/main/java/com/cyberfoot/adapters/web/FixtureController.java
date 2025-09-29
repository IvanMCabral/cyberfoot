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
        public Mono<Object> createFixtures(@RequestBody Map<String, String> body) {
            String clubId = body.get("clubId");
            // Buscar todos los clubes menos el propio
            return clubRepo.findAll()
                .filter(club -> !club.id().equals(clubId))
                .collectList()
                .flatMap(rivals -> {
                    List<Fixture> fixtures = new ArrayList<>();
                    for (Club rival : rivals) {
                        Fixture f = new Fixture(
                            UUID.randomUUID().toString(),
                            clubId,
                            rival.id(),
                            null,
                            "SCHEDULED",
                            0,
                            0,
                            null,
                            1
                        );
                        fixtures.add(f);
                        fixtureRepo.save(f).subscribe();
                    }
                    // Devolver los nombres de los rivales junto con los fixtures
                    List<Map<String, Object>> fixtureDtos = new ArrayList<>();
                    for (int i = 0; i < fixtures.size(); i++) {
                        Fixture fix = fixtures.get(i);
                        Club rival = rivals.get(i);
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id", fix.id());
                        dto.put("homeClubId", fix.homeClubId());
                        dto.put("awayClubId", fix.awayClubId());
                        dto.put("awayClubName", rival.name());
                        fixtureDtos.add(dto);
                    }
                    Map<String, Object> response = new HashMap<>();
                    response.put("fixtures", fixtureDtos);
                    return Mono.just(response);
                });
    }
}
