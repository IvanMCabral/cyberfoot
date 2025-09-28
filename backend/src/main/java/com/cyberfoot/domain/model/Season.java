package com.cyberfoot.domain.model;

// import java.util.UUID;

public class Season {
    private String id;
    private String name;
    private int yearStart;
    private int yearEnd;
    private boolean isActive;

    public Season(String id, String name, int yearStart, int yearEnd, boolean isActive) {
        this.id = id;
        this.name = name;
        this.yearStart = yearStart;
        this.yearEnd = yearEnd;
        this.isActive = isActive;
    }
    // Getters y setters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getYearStart() { return yearStart; }
    public int getYearEnd() { return yearEnd; }
    public boolean isActive() { return isActive; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setYearStart(int yearStart) { this.yearStart = yearStart; }
    public void setYearEnd(int yearEnd) { this.yearEnd = yearEnd; }
    public void setActive(boolean active) { isActive = active; }
}
