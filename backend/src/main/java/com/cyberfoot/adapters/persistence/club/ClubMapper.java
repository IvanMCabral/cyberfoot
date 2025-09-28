package com.cyberfoot.adapters.persistence.club;

import com.cyberfoot.domain.model.Club;

public class ClubMapper {
	public static Club toDomain(ClubEntity entity) {
		return new Club(
			entity.get_id(),
			entity.getName(),
			entity.getOverall()
		);
	}

	public static ClubEntity toEntity(Club club) {
		return new ClubEntity(
			club.id(),
			club.name(),
			club.overall()
		);
	}
}