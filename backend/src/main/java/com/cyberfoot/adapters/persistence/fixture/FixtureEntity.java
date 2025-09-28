package com.cyberfoot.adapters.persistence.fixture;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
//import java.util.UUID;

@Document(collection = "fixtures")
public class FixtureEntity {
	@Id
	private String _id; // MongoDB uses _id as string
	private String home_club_id;
	private String away_club_id;
	private Instant scheduled_at;
	private String status;
	private Integer goals_home;
	private Integer goals_away;
	private String season_id;
	private int matchday;

	public FixtureEntity() {}

	public FixtureEntity(String _id, String home_club_id, String away_club_id, Instant scheduled_at, String status, Integer goals_home, Integer goals_away, String season_id, int matchday) {
		this._id = _id;
		this.home_club_id = home_club_id;
		this.away_club_id = away_club_id;
		this.scheduled_at = scheduled_at;
		this.status = status;
		this.goals_home = goals_home;
		this.goals_away = goals_away;
		this.season_id = season_id;
		this.matchday = matchday;
	}

	public String get_id() { return _id; }
	public void set_id(String _id) { this._id = _id; }
	public String getHome_club_id() { return home_club_id; }
	public void setHome_club_id(String home_club_id) { this.home_club_id = home_club_id; }
	public String getAway_club_id() { return away_club_id; }
	public void setAway_club_id(String away_club_id) { this.away_club_id = away_club_id; }
	public Instant getScheduled_at() { return scheduled_at; }
	public void setScheduled_at(Instant scheduled_at) { this.scheduled_at = scheduled_at; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	public Integer getGoals_home() { return goals_home; }
	public void setGoals_home(Integer goals_home) { this.goals_home = goals_home; }
	public Integer getGoals_away() { return goals_away; }
	public void setGoals_away(Integer goals_away) { this.goals_away = goals_away; }
	public String getSeason_id() { return season_id; }
	public void setSeason_id(String season_id) { this.season_id = season_id; }
	public int getMatchday() { return matchday; }
	public void setMatchday(int matchday) { this.matchday = matchday; }
}