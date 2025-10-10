
package com.cyberfoot.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Document(collection = "game_sessions")
public class GameSession {
    private String playerName; // Nombre real del usuario principal
    private String campeonClubId;
    private String campeonDtId;
    private String ultimoClubId;
    private String ultimoDtId;
    @Id
    private String id;
    // Para MVP: solo un usuario, pero preparado para multiusuario
    private List<String> playerIds; // IDs de usuarios participantes
    private List<Fixture> fixture;
    private List<Club> clubs;
    private List<Player> players;
    private List<DirectorTecnico> dts;
    private List<Object> results; // Puedes definir un modelo específico
    private List<Object> standings; // Puedes definir un modelo específico
    private Instant createdAt;
    private Instant lastActive;
    // Equipo seleccionado por el usuario principal
    private String selectedTeamId;
    private int jornadaActual;

    public GameSession() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = Instant.now();
        this.lastActive = Instant.now();
        this.jornadaActual = 0;
    }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    // Getters y setters
    public String getCampeonClubId() { return campeonClubId; }
    public void setCampeonClubId(String campeonClubId) { this.campeonClubId = campeonClubId; }
    public String getCampeonDtId() { return campeonDtId; }
    public void setCampeonDtId(String campeonDtId) { this.campeonDtId = campeonDtId; }
    public String getUltimoClubId() { return ultimoClubId; }
    public void setUltimoClubId(String ultimoClubId) { this.ultimoClubId = ultimoClubId; }
    public String getUltimoDtId() { return ultimoDtId; }
    public void setUltimoDtId(String ultimoDtId) { this.ultimoDtId = ultimoDtId; }
    public int getJornadaActual() { return jornadaActual; }
    public void setJornadaActual(int jornadaActual) { this.jornadaActual = jornadaActual; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<String> getPlayerIds() { return playerIds; }
    public void setPlayerIds(List<String> playerIds) { this.playerIds = playerIds; }
    public List<Fixture> getFixture() { return fixture; }
    public void setFixture(List<Fixture> fixture) { this.fixture = fixture; }
    public List<Club> getClubs() { return clubs; }
    public void setClubs(List<Club> clubs) { this.clubs = clubs; }
    public List<Player> getPlayers() { return players; }
    public void setPlayers(List<Player> players) { this.players = players; }
    public List<DirectorTecnico> getDts() { return dts; }
    public void setDts(List<DirectorTecnico> dts) { this.dts = dts; }
    public List<Object> getResults() { return results; }
    public void setResults(List<Object> results) { this.results = results; }
    public List<Object> getStandings() { return standings; }
    public void setStandings(List<Object> standings) { this.standings = standings; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getLastActive() { return lastActive; }
    public void setLastActive(Instant lastActive) { this.lastActive = lastActive; }

    public String getSelectedTeamId() { return selectedTeamId; }
    public void setSelectedTeamId(String selectedTeamId) { this.selectedTeamId = selectedTeamId; }
}
