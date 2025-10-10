
package com.cyberfoot.adapters.persistence.fixture;

import com.cyberfoot.domain.model.Fixture;

public class FixtureMapper {
	public static Fixture toDomain(FixtureEntity entity) {
		   return new Fixture(
			   entity.get_id(),
			   entity.getHome_club_id(),
			   entity.getAway_club_id(),
			   entity.getScheduled_at(),
			   entity.getStatus(),
			   entity.getGoals_home(),
			   entity.getGoals_away(),
			   entity.getSeason_id(),
			   entity.getMatchday()
		   );
	}

	public static FixtureEntity toEntity(Fixture fixture) {
		   return new FixtureEntity(
			   fixture.getId(),
			   fixture.getHomeClubId(),
			   fixture.getAwayClubId(),
			   fixture.getScheduledAt(),
			   fixture.getStatus(),
			   fixture.getGoalsHome(),
			   fixture.getGoalsAway(),
			   fixture.getSeasonId(),
			   fixture.getMatchday()
		   );
	}
}