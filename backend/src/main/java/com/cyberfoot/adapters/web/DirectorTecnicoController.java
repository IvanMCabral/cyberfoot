package com.cyberfoot.adapters.web;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.cyberfoot.domain.model.DirectorTecnico;
import org.springframework.beans.factory.annotation.Autowired;
import com.cyberfoot.adapters.repository.DirectorTecnicoRepository;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/directores-tecnicos")
public class DirectorTecnicoController {
    @Autowired
    private DirectorTecnicoRepository dtRepository;

    @GetMapping
    public Flux<DirectorTecnico> getAll() {
        return dtRepository.findAll();
    }

    @PostMapping
    public Mono<DirectorTecnico> create(@RequestBody DirectorTecnico dt) {
        return dtRepository.save(dt);
    }


    // User-created DTs should NOT be persisted globally.
    // The following endpoint is disabled to prevent user DTs from being saved in the global repository.
    // @PostMapping("/usuario")
    // public Mono<DirectorTecnico> crearUsuario(@RequestBody DirectorTecnico usuario) {
    //     return dtRepository.save(usuario);
    // }

    @GetMapping("/usuario")
    public Mono<DirectorTecnico> getUsuario() {
        return dtRepository.findAll()
            .filter(dt -> dt.getNombre() != null && !dt.getNombre().startsWith("DT Aleatorio"))
            .next();
    }
}
