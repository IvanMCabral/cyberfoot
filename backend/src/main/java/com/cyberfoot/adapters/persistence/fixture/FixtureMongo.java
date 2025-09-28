package com.cyberfoot.adapters.persistence.fixture;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "fixtures")
public class FixtureMongo {
    @Id
    private String id;
    private String homeClubId;
    private String awayClubId;
    private Instant scheduledAt;
    private String status;
    private Integer goalsHome;
    private Integer goalsAway;

    public FixtureMongo() {}

    public FixtureMongo(String id, String homeClubId, String awayClubId, Instant scheduledAt, String status, Integer goalsHome, Integer goalsAway) {
        this.id = id;
        this.homeClubId = homeClubId;
        this.awayClubId = awayClubId;
        this.scheduledAt = scheduledAt;
        this.status = status;
        this.goalsHome = goalsHome;
        this.goalsAway = goalsAway;
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
}