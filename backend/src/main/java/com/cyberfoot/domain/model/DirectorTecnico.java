
package com.cyberfoot.domain.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectorTecnico {
    private String id;
    private String nombre;
    private String email;
    private double porcentajeVictorias;
    private int victorias; // Número de victorias
    private double prestigio;
    private String clubId;

    public DirectorTecnico() {
    this.id = UUID.randomUUID().toString();
    this.victorias = 0;
    }

    public DirectorTecnico(String nombre, String email, double porcentajeVictorias, double prestigio, String clubId) {
    this.id = UUID.randomUUID().toString();
    this.nombre = nombre;
    this.email = email;
    this.porcentajeVictorias = porcentajeVictorias;
    this.victorias = 0;
    this.prestigio = prestigio;
    this.clubId = clubId;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public double getPorcentajeVictorias() { return porcentajeVictorias; }
    public int getVictorias() { return victorias; }
    public void setVictorias(int victorias) { this.victorias = victorias; }
    public void setPorcentajeVictorias(double porcentajeVictorias) { this.porcentajeVictorias = porcentajeVictorias; }
    public double getPrestigio() { return prestigio; }
    public void setPrestigio(double prestigio) { this.prestigio = prestigio; }
    public String getClubId() { return clubId; }
    public void setClubId(String clubId) { this.clubId = clubId; }

    public void setId(String id) { this.id = id; }
}
