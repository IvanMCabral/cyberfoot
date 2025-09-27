package com.cyberfoot.adapters.persistence.fixture;

import com.cyberfoot.domain.model.Fixture;

public class FixtureMapper {
	public static Fixture toDomain(FixtureEntity entity) {
		return new Fixture(
			entity.getId(),
			entity.getHomeClubId(),
			entity.getAwayClubId(),
			entity.getScheduledAt(),
			entity.getStatus(),
			entity.getGoalsHome(),
			entity.getGoalsAway()
		);
	}

	public static FixtureEntity toEntity(Fixture fixture) {
		return new FixtureEntity(
			fixture.id(),
			fixture.homeClubId(),
			fixture.awayClubId(),
			fixture.scheduledAt(),
			fixture.status(),
			fixture.goalsHome(),
			fixture.goalsAway()
		);
	}
}