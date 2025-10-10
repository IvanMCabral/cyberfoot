package com.cyberfoot.domain.model;

import java.time.Instant;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Fixture {
    private String id;
    private String homeClubId;
    private String awayClubId;
    private Instant scheduledAt;
    private String status;
    private Integer goalsHome;
    private Integer goalsAway;
    private String seasonId;
    private int matchday;

    public Fixture() {}

    public Fixture(String id, String homeClubId, String awayClubId, Instant scheduledAt, String status, Integer goalsHome, Integer goalsAway, String seasonId, int matchday) {
        this.id = id;
        this.homeClubId = homeClubId;
        this.awayClubId = awayClubId;
        this.scheduledAt = scheduledAt;
        this.status = status;
        this.goalsHome = goalsHome;
        this.goalsAway = goalsAway;
        this.seasonId = seasonId;
        this.matchday = matchday;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getHomeClubId() { return homeClubId; }
    public void setHomeClubId(String homeClubId) { this.homeClubId = homeClubId; }
    public String getAwayClubId() { return awayClubId; }
    public void setAwayClubId(String awayClubId) { this.awayClubId = awayClubId; }
    public Instant getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(Instant scheduledAt) { this.scheduledAt = scheduledAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getGoalsHome() { return goalsHome; }
    public void setGoalsHome(Integer goalsHome) { this.goalsHome = goalsHome; }
    public Integer getGoalsAway() { return goalsAway; }
    public void setGoalsAway(Integer goalsAway) { this.goalsAway = goalsAway; }
    public String getSeasonId() { return seasonId; }
    public void setSeasonId(String seasonId) { this.seasonId = seasonId; }
    public int getMatchday() { return matchday; }
    public void setMatchday(int matchday) { this.matchday = matchday; }

    @JsonProperty("matchResult")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public MatchResult getMatchResult() {
        if (goalsHome != null && goalsAway != null && "FINISHED".equalsIgnoreCase(status)) {
            return new MatchResult(goalsHome, goalsAway);
        }
        return null;
    }

    public static class MatchResult {
        private Integer goalsHome;
        private Integer goalsAway;
        public MatchResult(Integer goalsHome, Integer goalsAway) {
            this.goalsHome = goalsHome;
            this.goalsAway = goalsAway;
        }
        public Integer getGoalsHome() { return goalsHome; }
        public Integer getGoalsAway() { return goalsAway; }
    }
}
// TODO[M1.1]: Agregar atributos extra en M2
