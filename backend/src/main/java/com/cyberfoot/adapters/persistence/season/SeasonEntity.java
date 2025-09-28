package com.cyberfoot.adapters.persistence.season;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "season")
public class SeasonEntity {
    @Id
    private String id;
    private String name;
    private int yearStart;
    private int yearEnd;
    private boolean isActive;

    public SeasonEntity() {}
    public SeasonEntity(String id, String name, int yearStart, int yearEnd, boolean isActive) {
        this.id = id;
        this.name = name;
        this.yearStart = yearStart;
        this.yearEnd = yearEnd;
        this.isActive = isActive;
    }
    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getYearStart() { return yearStart; }
    public void setYearStart(int yearStart) { this.yearStart = yearStart; }
    public int getYearEnd() { return yearEnd; }
    public void setYearEnd(int yearEnd) { this.yearEnd = yearEnd; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
