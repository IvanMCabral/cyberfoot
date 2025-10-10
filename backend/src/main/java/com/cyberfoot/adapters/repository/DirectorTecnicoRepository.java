package com.cyberfoot.adapters.repository;

import com.cyberfoot.domain.model.DirectorTecnico;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorTecnicoRepository extends ReactiveMongoRepository<DirectorTecnico, String> {
	// NOTE: User-created DTs (usuario) should NOT be saved in this repository. They must only exist in the session (GameSession.dts).
}
