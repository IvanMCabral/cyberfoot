package com.cyberfoot.adapters.web;

import com.cyberfoot.domain.model.Player;
import com.cyberfoot.adapters.persistence.player.PlayerMongoRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class PlayerController {
    private final PlayerMongoRepository playerRepo;

    public PlayerController(PlayerMongoRepository playerRepo) {
        this.playerRepo = playerRepo;
    }
        @GetMapping("/players/ping")
        public String pingPlayers() {
            System.out.println("[PlayerController] /api/players/ping invoked");
            return "PlayerController ping OK";
        }

    @GetMapping("/players")
        public Flux<Player> getPlayersByClub(@RequestParam String clubId) {
            Flux<Player> players = playerRepo.findByClubId(clubId)
                .switchIfEmpty(Flux.empty());
            players.doOnNext(player -> System.out.println("[PlayerController] player: " + player));
            return players;
        }

    @PutMapping("/players/{id}")
    public Mono<Player> updatePlayer(@PathVariable String id, @RequestBody Player player) {
        return playerRepo.findById(id)
            .flatMap(existing -> {
                Player updated = new Player(
                    id,
                    player.clubId(),
                    player.name(),
                    player.rating()
                );
                return playerRepo.save(updated);
            });
    }
}
