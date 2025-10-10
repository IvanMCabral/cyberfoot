package com.cyberfoot.adapters.web;

import com.cyberfoot.domain.model.Club;
import com.cyberfoot.domain.ports.ClubRepository;
// import com.cyberfoot.domain.model.DirectorTecnico;
import com.cyberfoot.adapters.repository.DirectorTecnicoRepository;
import java.util.List;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ClubController {
    private final ClubRepository clubRepo;
    private final DirectorTecnicoRepository dtRepo;

    public ClubController(ClubRepository clubRepo, DirectorTecnicoRepository dtRepo) {
        this.clubRepo = clubRepo;
        this.dtRepo = dtRepo;
    }
        @GetMapping("/clubs/pingtest")
        public String pingTestClubs() {
            System.out.println("[ClubController] /api/clubs/pingtest invoked");
            return "ClubController pingtest OK";
        }
    // Asignar DTs fijos a clubes sin DT
    @PostMapping("/clubs/asignar-dts")
    public Flux<Club> asignarDTs() {
        // Obtener todos los DTs fijos
        return dtRepo.findAll()
            .filter(dt -> dt.getNombre() != null)
            .collectList()
            .flatMapMany(dts ->
                clubRepo.findAll()
                    .collectList()
                    .flatMapMany(clubs -> {
                        List<Club> actualizados = new ArrayList<>();
                        int dtIndex = 0;
                        for (Club club : clubs) {
                            if (club.directorTecnicoId() == null && dtIndex < dts.size()) {
                                // Asignar DT fijo al club sin DT
                                Club actualizado = new Club(
                                    club.id(),
                                    club.name(),
                                    club.overall(),
                                    dts.get(dtIndex).getId(),
                                    new ArrayList<>() // Sin jugadores
                                );
                                actualizados.add(actualizado);
                                dtIndex++;
                            } else {
                                actualizados.add(club);
                            }
                        }
                        // Guardar todos los clubes actualizados
                        return Flux.fromIterable(actualizados)
                            .flatMap(clubRepo::save)
                            .cast(Club.class);
                    })
            );
    }

    @GetMapping("/clubs/{id}")
    public Mono<Club> getClub(@PathVariable String id) {
        return clubRepo.findById(id)
            .switchIfEmpty(Mono.empty());
    }

    @GetMapping("/clubs")
        public Flux<Club> getAllClubs() {
            Flux<Club> clubs = clubRepo.findAll();
            clubs.doOnNext(club -> System.out.println("[ClubController] club: " + club));
            return clubs;
        }

    @PostMapping("/clubs")
    public Mono<Club> createClub(@RequestBody Club club) {
        return clubRepo.save(club);
    }

    @PutMapping("/clubs/{id}")
    public Mono<Club> updateClub(@PathVariable String id, @RequestBody Club club) {
        return clubRepo.findById(id)
            .flatMap(existing -> {
                Club updated = new Club(
                    id,
                    club.name(),
                    club.overall(),
                    club.directorTecnicoId(),
                    club.players() != null ? club.players() : new ArrayList<>()
                );
                return clubRepo.save(updated);
            });
    }
}
