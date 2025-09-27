package com.cyberfoot.adapters.web;

import com.cyberfoot.domain.model.Fixture;
import com.cyberfoot.domain.ports.FixtureRepository;
import com.cyberfoot.adapters.persistence.fixture.FixtureRepositoryAdapter;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class FixtureController {
    private final FixtureRepository fixtureRepo;

    public FixtureController(FixtureRepository fixtureRepo) {
        this.fixtureRepo = fixtureRepo;
    }

    @GetMapping("/fixtures/{id}")
    public Mono<Fixture> getFixture(@PathVariable UUID id) {
        return fixtureRepo.findById(id);
    }

    @GetMapping("/fixtures")
    public Flux<Fixture> getAllFixtures() {
        return ((FixtureRepositoryAdapter) fixtureRepo).findAll();
    }

    @PostMapping("/fixtures")
    public Mono<Fixture> createFixture(@RequestBody Fixture fixture) {
        return ((FixtureRepositoryAdapter) fixtureRepo).save(fixture);
    }
}
