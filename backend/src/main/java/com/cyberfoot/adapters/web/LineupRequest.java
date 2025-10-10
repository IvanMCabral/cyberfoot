package com.cyberfoot.adapters.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class LineupRequest {
    private String homeClubId;
    private String awayClubId;
    private List<Integer> homeRatings;
    private List<Integer> awayRatings;

    public LineupRequest() {}

    public String getHomeClubId() { return homeClubId; }
    public void setHomeClubId(String homeClubId) { this.homeClubId = homeClubId; }

    public String getAwayClubId() { return awayClubId; }
    public void setAwayClubId(String awayClubId) { this.awayClubId = awayClubId; }

    public List<Integer> getHomeRatings() { return homeRatings; }
    public void setHomeRatings(List<Integer> homeRatings) { this.homeRatings = homeRatings; }

    public List<Integer> getAwayRatings() { return awayRatings; }
    public void setAwayRatings(List<Integer> awayRatings) { this.awayRatings = awayRatings; }
}
