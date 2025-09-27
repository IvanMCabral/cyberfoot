package com.cyberfoot.adapters.persistence.club;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.util.UUID;

@Table("club")
public class ClubEntity {
	@Id
	private UUID id;
	private String name;
	private int overall;

	public ClubEntity() {}

	public ClubEntity(UUID id, String name, int overall) {
		this.id = id;
		this.name = name;
		this.overall = overall;
	}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public int getOverall() { return overall; }
	public void setOverall(int overall) { this.overall = overall; }
}