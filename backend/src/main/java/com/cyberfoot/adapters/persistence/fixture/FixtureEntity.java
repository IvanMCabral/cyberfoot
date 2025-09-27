package com.cyberfoot.adapters.persistence.fixture;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;
import java.util.UUID;

@Table("fixture")
public class FixtureEntity {
	@Id
	private UUID id;
	private UUID homeClubId;
	private UUID awayClubId;
	private Instant scheduledAt;
	private String status;
	private Integer goalsHome;
	private Integer goalsAway;

	public FixtureEntity() {}

	public FixtureEntity(UUID id, UUID homeClubId, UUID awayClubId, Instant scheduledAt, String status, Integer goalsHome, Integer goalsAway) {
		this.id = id;
		this.homeClubId = homeClubId;
		this.awayClubId = awayClubId;
		this.scheduledAt = scheduledAt;
		this.status = status;
		this.goalsHome = goalsHome;
		this.goalsAway = goalsAway;
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public UUID getHomeClubId() { return homeClubId; }
	public void setHomeClubId(UUID homeClubId) { this.homeClubId = homeClubId; }
	public UUID getAwayClubId() { return awayClubId; }
	public void setAwayClubId(UUID awayClubId) { this.awayClubId = awayClubId; }
	public Instant getScheduledAt() { return scheduledAt; }
	public void setScheduledAt(Instant scheduledAt) { this.scheduledAt = scheduledAt; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public Integer getGoalsHome() { return goalsHome; }
	public void setGoalsHome(Integer goalsHome) { this.goalsHome = goalsHome; }
	public Integer getGoalsAway() { return goalsAway; }
	public void setGoalsAway(Integer goalsAway) { this.goalsAway = goalsAway; }
}