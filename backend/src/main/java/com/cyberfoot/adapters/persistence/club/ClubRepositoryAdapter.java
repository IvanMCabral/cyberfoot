
package com.cyberfoot.adapters.persistence.club;
import org.springframework.stereotype.Component;


import com.cyberfoot.domain.ports.ClubRepository;
import com.cyberfoot.domain.model.Club;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Component
public class ClubRepositoryAdapter implements ClubRepository {
	private final ClubRepositoryR2dbc clubRepoR2dbc;

	public ClubRepositoryAdapter(ClubRepositoryR2dbc clubRepoR2dbc) {
		this.clubRepoR2dbc = clubRepoR2dbc;
	}
	@Override
	public Mono<Club> findById(UUID id) {
		return clubRepoR2dbc.findById(id)
			.map(ClubMapper::toDomain);
	}

	public Mono<Club> save(Club club) {
		ClubEntity entity = ClubMapper.toEntity(club);
		return clubRepoR2dbc.save(entity)
			.map(ClubMapper::toDomain);
	}

	public reactor.core.publisher.Flux<Club> findAll() {
		return clubRepoR2dbc.findAll()
			.map(ClubMapper::toDomain);
	}
}