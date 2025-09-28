package com.cyberfoot.adapters.web;

import com.cyberfoot.domain.model.Player;
import com.cyberfoot.adapters.persistence.player.PlayerMongoRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class PlayerController {
    private final PlayerMongoRepository playerRepo;

    public PlayerController(PlayerMongoRepository playerRepo) {
        this.playerRepo = playerRepo;
    }

    @GetMapping("/players")
    public Flux<Player> getPlayersByClub(@RequestParam String clubId) {
        return playerRepo.findByClubId(clubId)
            .switchIfEmpty(Flux.empty());
    }
}
