package com.cyberfoot.domain.model;

import java.util.UUID;

public class LeagueRow {
    private UUID seasonId;
    private UUID clubId;
    private String clubName;
    private int gf;
    private int gc;
    private int pts;
    private int w;
    private int d;
    private int l;

    public LeagueRow(UUID seasonId, UUID clubId, String clubName, int gf, int gc, int pts, int w, int d, int l) {
        this.seasonId = seasonId;
        this.clubId = clubId;
        this.clubName = clubName;
        this.gf = gf;
        this.gc = gc;
        this.pts = pts;
        this.w = w;
        this.d = d;
        this.l = l;
    }
    // Getters y setters
    public UUID getSeasonId() { return seasonId; }
    public UUID getClubId() { return clubId; }
    public String getClubName() { return clubName; }
    public int getGf() { return gf; }
    public int getGc() { return gc; }
    public int getPts() { return pts; }
    public int getW() { return w; }
    public int getD() { return d; }
    public int getL() { return l; }
    public void setSeasonId(UUID seasonId) { this.seasonId = seasonId; }
    public void setClubId(UUID clubId) { this.clubId = clubId; }
    public void setClubName(String clubName) { this.clubName = clubName; }
    public void setGf(int gf) { this.gf = gf; }
    public void setGc(int gc) { this.gc = gc; }
    public void setPts(int pts) { this.pts = pts; }
    public void setW(int w) { this.w = w; }
    public void setD(int d) { this.d = d; }
    public void setL(int l) { this.l = l; }
}
