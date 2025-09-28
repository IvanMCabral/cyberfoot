package com.cyberfoot.adapters.web;

import com.cyberfoot.domain.model.Club;
import com.cyberfoot.domain.ports.ClubRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ClubController {
    private final ClubRepository clubRepo;

    public ClubController(ClubRepository clubRepo) {
        this.clubRepo = clubRepo;
    }

    @GetMapping("/clubs/{id}")
    public Mono<Club> getClub(@PathVariable String id) {
        return clubRepo.findById(id)
            .switchIfEmpty(Mono.empty());
    }

    @GetMapping("/clubs")
    public Flux<Club> getAllClubs() {
        return clubRepo.findAll();
    }

    @PostMapping("/clubs")
    public Mono<Club> createClub(@RequestBody Club club) {
        return clubRepo.save(club);
    }
}
