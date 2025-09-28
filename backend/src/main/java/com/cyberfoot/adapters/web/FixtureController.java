package com.cyberfoot.adapters.web;

import com.cyberfoot.domain.model.Fixture;
import com.cyberfoot.domain.ports.FixtureRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class FixtureController {
    private final FixtureRepository fixtureRepo;

    public FixtureController(FixtureRepository fixtureRepo) {
        this.fixtureRepo = fixtureRepo;
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
    public Mono<Fixture> createFixture(@RequestBody Fixture fixture) {
        // If needed, implement save in FixtureRepository and its adapter
        // For now, return Mono.empty() or implement as needed
        return Mono.empty();
    }
}
