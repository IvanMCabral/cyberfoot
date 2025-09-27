
package com.cyberfoot.adapters.persistence.fixture;
import org.springframework.stereotype.Component;


import com.cyberfoot.domain.ports.FixtureRepository;
import com.cyberfoot.domain.model.Fixture;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Component
public class FixtureRepositoryAdapter implements FixtureRepository {
	private final FixtureRepositoryR2dbc fixtureRepoR2dbc;

	public FixtureRepositoryAdapter(FixtureRepositoryR2dbc fixtureRepoR2dbc) {
		this.fixtureRepoR2dbc = fixtureRepoR2dbc;
	}
	@Override
	public Mono<Fixture> findById(UUID id) {
		return fixtureRepoR2dbc.findById(id)
			.map(FixtureMapper::toDomain);
	}

	@Override
	public Mono<Fixture> save(Fixture fixture) {
		FixtureEntity entity = FixtureMapper.toEntity(fixture);
		return fixtureRepoR2dbc.save(entity)
			.map(FixtureMapper::toDomain);
	}

	@Override
	public Flux<Fixture> findAll() {
		return fixtureRepoR2dbc.findAll()
			.map(FixtureMapper::toDomain);
	}
}
// ...existing code from FixtureRepositoryAdapter.java...