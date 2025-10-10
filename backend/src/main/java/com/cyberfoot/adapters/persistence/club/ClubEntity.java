package com.cyberfoot.adapters.persistence.club;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
//import java.util.UUID;

@Document(collection = "clubs")
public class ClubEntity {
    @Id
    private String _id; // MongoDB uses _id as string
    private String name;
    private int overall;
    private String directorTecnicoId;

    public ClubEntity() {}

    public ClubEntity(String _id, String name, int overall, String directorTecnicoId) {
        this._id = _id;
        this.name = name;
        this.overall = overall;
        this.directorTecnicoId = directorTecnicoId;
    }

    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getOverall() { return overall; }
    public void setOverall(int overall) { this.overall = overall; }
    public String getDirectorTecnicoId() { return directorTecnicoId; }
    public void setDirectorTecnicoId(String directorTecnicoId) { this.directorTecnicoId = directorTecnicoId; }
}