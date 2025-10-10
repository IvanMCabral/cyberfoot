
package com.cyberfoot.adapters.web;
import com.cyberfoot.adapters.persistence.gamesession.GameSessionRepository;
import com.cyberfoot.adapters.persistence.player.PlayerMongoRepository;
import com.cyberfoot.adapters.repository.DirectorTecnicoRepository;
import com.cyberfoot.domain.model.Club;
import com.cyberfoot.domain.model.Fixture;
import com.cyberfoot.domain.model.GameSession;
import com.cyberfoot.domain.model.DirectorTecnico;
import com.cyberfoot.domain.ports.ClubRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;



@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins = "http://localhost:4200")
public class GameSessionController {

    private static final Logger logger = LogManager.getLogger(GameSessionController.class);

    private final DirectorTecnicoRepository dtRepository;
    private final ClubRepository clubRepo;
    private final GameSessionRepository sessionRepo;
    private final PlayerMongoRepository playerRepo;

    public GameSessionController(GameSessionRepository sessionRepo,
                                 PlayerMongoRepository playerRepo,
                                 ClubRepository clubRepo,
                                 DirectorTecnicoRepository dtRepository) {
        this.sessionRepo = sessionRepo;
        this.playerRepo = playerRepo;
        this.clubRepo = clubRepo;
        this.dtRepository = dtRepository;
    }

        // PATCH: Actualizar jornada actual de la sesión
    @PatchMapping("/game/{id}/jornada")
    public Mono<ResponseEntity<GameSession>> updateJornadaActual(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        final Integer[] jornadaActualHolder = new Integer[1];
        if (payload != null && payload.containsKey("jornadaActual")) {
            Object value = payload.get("jornadaActual");
            if (value instanceof Integer) {
                jornadaActualHolder[0] = (Integer) value;
            } else if (value instanceof Number) {
                jornadaActualHolder[0] = ((Number) value).intValue();
            }
        }
        if (jornadaActualHolder[0] == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        final Integer jornadaActualFinal = jornadaActualHolder[0];
        return sessionRepo.findById(id)
            .flatMap(session -> {
                try {
                    java.lang.reflect.Method setJornada = session.getClass().getMethod("setJornadaActual", int.class);
                    setJornada.invoke(session, jornadaActualFinal);
                } catch (Exception e) {
                    // Si la sesión no tiene el método, ignorar
                }
                return sessionRepo.save(session).map(ResponseEntity::ok);
            })
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    // PATCH: Guardar selección de equipo en la sesión y poblar DTs si aún no existen
    @PatchMapping("/game/{id}/team")
    public Mono<ResponseEntity<Map<String, Object>>> setSelectedTeam(@PathVariable String id,
                                                                    @RequestParam String teamId) {
        logger.info("[GameSessionController] PATCH /api/session/game/{}/team -> teamId: {}", id, teamId);

        return sessionRepo.findById(id)
            .flatMap(session -> {
                logger.info("[GameSessionController] sessionId: {} | selectedTeamId antes: {}", session.getId(), session.getSelectedTeamId());
                session.setSelectedTeamId(teamId);
                session.setLastActive(Instant.now());
                Mono<List<Club>> clubsWithPlayersMono = clubRepo.findAll().collectList()
                    .flatMap(clubs -> Flux.fromIterable(clubs)
                        .flatMap(club -> playerRepo.findByClubId(club.id()).collectList()
                            .map(players -> {
                                if (players == null) players = new ArrayList<>();
                                return new Club(
                                    club.id(),
                                    club.name(),
                                    club.overall(),
                                    club.directorTecnicoId(),
                                    players
                                );
                            })
                        ).collectList()
                    );
                return clubsWithPlayersMono.flatMap(clubsWithPlayers -> {
                    session.setClubs(clubsWithPlayers);
                    // Repoblar lista de todos los jugadores
                    List<com.cyberfoot.domain.model.Player> allPlayers = new ArrayList<>();
                    for (Club c : clubsWithPlayers) {
                        if (c.players() != null) allPlayers.addAll(c.players());
                    }
                    session.setPlayers(allPlayers);
                    // Si no hay DTs, poblarlos como antes
                    // User-created DTs (usuario) are ONLY added to the session (session.setDts) and are NOT persisted globally.
                    // Only pre-existing DTs from the global pool (dtRepository.findAll) are used for rivals and desempleados.
                    if (session.getDts() == null || session.getDts().isEmpty()) {
                        return dtRepository.findAll().collectList().flatMap(dtPool -> {
                            Collections.shuffle(dtPool);
                            List<DirectorTecnico> dts = new ArrayList<>();
                            // 1) DT del usuario para el club elegido
                            Club userClub = clubsWithPlayers.stream().filter(c -> c.id().equals(teamId)).findFirst().orElse(null);
                            if (userClub == null) {
                                logger.warn("[GameSessionController] Club no encontrado para teamId {}", teamId);
                                Map<String, Object> error = new HashMap<>();
                                error.put("error", "Club no encontrado");
                                return Mono.just(ResponseEntity.badRequest().body(error));
                            }
                            DirectorTecnico userDt = new DirectorTecnico();
                            userDt.setId(UUID.randomUUID().toString());
                            String nombreUsuario = null;
                            try {
                                java.lang.reflect.Method getPlayerName = session.getClass().getMethod("getPlayerName");
                                Object nombreObj = getPlayerName.invoke(session);
                                if (nombreObj != null) nombreUsuario = nombreObj.toString();
                            } catch (Exception ignored) {}
                            if (nombreUsuario == null || nombreUsuario.isEmpty()) {
                                nombreUsuario = (session.getPlayerIds() != null && !session.getPlayerIds().isEmpty()) ? session.getPlayerIds().get(0) : "Usuario";
                            }
                            userDt.setNombre(nombreUsuario);
                            userDt.setPrestigio(50);
                            userDt.setVictorias(0);
                            userDt.setEmail(null);
                            userDt.setPorcentajeVictorias(0);
                            userDt.setClubId(teamId);
                            dts.add(userDt);
                            // 2) DTs rivales para el resto de clubes
                            List<Club> clubesRivales = new ArrayList<>(clubsWithPlayers);
                            clubesRivales.removeIf(c -> c.id().equals(teamId));
                            Collections.shuffle(dtPool);
                            int dtIdx = 0;
                            for (Club club : clubesRivales) {
                                if (dtIdx >= dtPool.size()) break;
                                DirectorTecnico dtReal = dtPool.get(dtIdx);
                                DirectorTecnico dtAsignado = new DirectorTecnico();
                                dtAsignado.setId(dtReal.getId());
                                dtAsignado.setNombre(dtReal.getNombre());
                                dtAsignado.setPrestigio(dtReal.getPrestigio());
                                dtAsignado.setVictorias(0);
                                dtAsignado.setEmail(dtReal.getEmail());
                                dtAsignado.setPorcentajeVictorias(0);
                                dtAsignado.setClubId(club.id());
                                dts.add(dtAsignado);
                                dtIdx++;
                            }
                            // 3) DTs sobrantes quedan desempleados
                            if (dtPool.size() > dtIdx) {
                                for (int i = dtIdx; i < dtPool.size(); i++) {
                                    DirectorTecnico dtReal = dtPool.get(i);
                                    DirectorTecnico dtDesempleado = new DirectorTecnico();
                                    dtDesempleado.setId(dtReal.getId());
                                    dtDesempleado.setNombre(dtReal.getNombre());
                                    dtDesempleado.setPrestigio(dtReal.getPrestigio());
                                    dtDesempleado.setVictorias(0);
                                    dtDesempleado.setEmail(dtReal.getEmail());
                                    dtDesempleado.setPorcentajeVictorias(0);
                                    dtDesempleado.setClubId(null);
                                    dts.add(dtDesempleado);
                                }
                            }
                            session.setDts(dts);
                            return sessionRepo.save(session).map(savedSession -> {
                                Map<String, Object> result = new HashMap<>();
                                result.put("session", savedSession);
                                result.put("dts", dts);
                                return ResponseEntity.ok(result);
                            });
                        });
                    }
                    // Ya había DTs: solo guardar elección y clubes actualizados
                    return sessionRepo.save(session).map(savedSession -> {
                        Map<String, Object> result = new HashMap<>();
                        result.put("session", savedSession);
                        result.put("dts", savedSession.getDts());
                        return ResponseEntity.ok(result);
                    });
                });
            })
        .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/ping")
    public String pingSession() {
        logger.info("[GameSessionController] /api/session/ping invoked");
        return "GameSessionController ping OK";
    }

    @PostMapping
    public Mono<GameSession> createSession(@RequestParam(required = false) String userId,
                                           @RequestParam(required = false) String playerName) {
        return clubRepo.findAll().collectList().flatMap(clubs -> {
            Collections.shuffle(clubs); // Fixture aleatorio

            List<Fixture> fixture = generateRoundRobinFixture(clubs);

            GameSession session = new GameSession();

            // Para MVP: un usuario; preparado para multiusuario
            List<String> playerIds = new ArrayList<>();
            if (userId != null) playerIds.add(userId);
            session.setPlayerIds(playerIds);
            // Guardar el nombre real del usuario si viene
            try {
                java.lang.reflect.Method setPlayerName = session.getClass().getMethod("setPlayerName", String.class);
                if (playerName != null && !playerName.isEmpty()) {
                    setPlayerName.invoke(session, playerName);
                }
            } catch (Exception ignored) {}

            // Poblar jugadores por club
            return Flux.fromIterable(clubs)
                .flatMap(club -> playerRepo.findByClubId(club.id()).collectList()
                    .map(players -> {
                        if (players == null) players = new ArrayList<>();
                        return new Club(
                            club.id(),
                            club.name(),
                            club.overall(),
                            club.directorTecnicoId(),
                            players
                        );
                    })
                ).collectList()
                .flatMap(clubsWithPlayers -> {
                    session.setClubs(clubsWithPlayers);
                    session.setFixture(fixture);

                    // Guardar todos los jugadores en la sesión
                    List<com.cyberfoot.domain.model.Player> allPlayers = new ArrayList<>();
                    for (Club c : clubsWithPlayers) {
                        if (c.players() != null) allPlayers.addAll(c.players());
                    }
                    session.setPlayers(allPlayers);

                    // Log por fecha
                    Map<Integer, List<Fixture>> roundsMap = new HashMap<>();
                    for (Fixture f : fixture) {
                        roundsMap.computeIfAbsent(f.getMatchday(), k -> new ArrayList<>()).add(f);
                    }
                    for (int rnd = 1; rnd <= roundsMap.size(); rnd++) {
                        List<Fixture> roundMatches = roundsMap.get(rnd);
                        if (roundMatches != null && !roundMatches.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("[Fixture] Fecha ").append(rnd).append(":\n");
                            for (Fixture f : roundMatches) {
                                sb.append("  ").append(f.getHomeClubId()).append(" vs ").append(f.getAwayClubId()).append("\n");
                            }
                            logger.info(sb.toString());
                        }
                    }

                    return sessionRepo.save(session);
                });
        });
    }

    // Genera un fixture round-robin (todos contra todos)
    private List<Fixture> generateRoundRobinFixture(List<Club> clubs) {
        List<Fixture> fixtures = new ArrayList<>();
        int n = clubs.size();
        List<Club> clubList = new ArrayList<>(clubs);
        if (n % 2 != 0) clubList.add(null); // Si impar, agregar dummy

        int numRounds = clubList.size() - 1;
        int numMatchesPerRound = clubList.size() / 2;

        for (int round = 0; round < numRounds; round++) {
            for (int match = 0; match < numMatchesPerRound; match++) {
                Club home = clubList.get(match);
                Club away = clubList.get(clubList.size() - 1 - match);
                if (home != null && away != null) {
                    Fixture f = new Fixture(
                        UUID.randomUUID().toString(),
                        home.id(),
                        away.id(),
                        null,
                        "SCHEDULED",
                        null,
                        null,
                        null,
                        round + 1
                    );
                    fixtures.add(f);
                }
            }
            // Rotación para la siguiente ronda
            if (clubList.size() > 2) {
                Club last = clubList.remove(clubList.size() - 1);
                clubList.add(1, last);
            }
        }
        return fixtures;
    }

    // Limpia sesiones inactivas (>24h)
    @DeleteMapping("/cleanup")
    public Mono<Void> cleanupInactiveSessions() {
        Instant threshold = Instant.now().minus(java.time.Duration.ofHours(24));
        return sessionRepo.deleteByLastActiveBefore(threshold);
    }

    // GET: Obtener la sesión por id (con fix defensivo en players)
    @GetMapping("/game/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> getGameById(@PathVariable String id) {
        logger.info("[GameSessionController] GET /api/session/game/{} invoked", id);

        return sessionRepo.findById(id)
            .map(session -> {
                // Fix defensivo: asegurar lists no nulas
                if (session.getClubs() != null) {
                    for (int i = 0; i < session.getClubs().size(); i++) {
                        Club club = session.getClubs().get(i);
                        if (club != null && club.players() == null) {
                            Club fixed = new Club(
                                club.id(),
                                club.name(),
                                club.overall(),
                                club.directorTecnicoId(),
                                new java.util.ArrayList<>()
                            );
                            session.getClubs().set(i, fixed);
                        }
                        // Loguear los jugadores de cada club
                        if (club != null) {
                            logger.info("[DEBUG] Club {} tiene {} jugadores", club.name(), club.players() != null ? club.players().size() : 0);
                            if (club.players() != null) {
                                for (var p : club.players()) {
                                    logger.info("[DEBUG]   - {} (rating: {})", p.name(), p.rating());
                                }
                            }
                        }
                    }
                }
                logger.info("[GameSessionController] GET /api/session/game/{} -> found", id);
                List<DirectorTecnico> dts = session.getDts() != null ? session.getDts() : new ArrayList<>();
                // Calcular standings (tabla de posiciones)
                List<Map<String, Object>> standings = calcularTablaPosiciones(session.getClubs(), session.getFixture());
                Map<String, Object> result = new HashMap<>();
                result.put("session", session);
                result.put("dts", dts);
                result.put("standings", standings);
                return ResponseEntity.ok(result);
            })
        .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    // Calcula la tabla de posiciones a partir de los fixtures y clubes
    private static List<Map<String, Object>> calcularTablaPosiciones(List<Club> clubs, List<Fixture> fixtures) {
        Map<String, Map<String, Object>> tabla = new HashMap<>();
        if (clubs != null) {
            for (Club club : clubs) {
                Map<String, Object> row = new HashMap<>();
                row.put("clubId", club.id());
                row.put("clubName", club.name());
                row.put("pj", 0); // Partidos jugados
                row.put("pg", 0); // Ganados
                row.put("pe", 0); // Empatados
                row.put("pp", 0); // Perdidos
                row.put("gf", 0); // Goles a favor
                row.put("gc", 0); // Goles en contra
                row.put("pts", 0); // Puntos
                tabla.put(club.id(), row);
            }
        }
        if (fixtures != null) {
            for (Fixture f : fixtures) {
                if (f.getStatus() == null || !"FINISHED".equalsIgnoreCase(f.getStatus())) continue;
                String homeId = f.getHomeClubId();
                String awayId = f.getAwayClubId();
                Integer gh = f.getGoalsHome();
                Integer ga = f.getGoalsAway();
                if (homeId == null || awayId == null || gh == null || ga == null) continue;
                Map<String, Object> home = tabla.get(homeId);
                Map<String, Object> away = tabla.get(awayId);
                if (home == null || away == null) continue;
                // Partidos jugados
                home.put("pj", (int)home.get("pj") + 1);
                away.put("pj", (int)away.get("pj") + 1);
                // Goles
                home.put("gf", (int)home.get("gf") + gh);
                home.put("gc", (int)home.get("gc") + ga);
                away.put("gf", (int)away.get("gf") + ga);
                away.put("gc", (int)away.get("gc") + gh);
                // Resultado
                if (gh > ga) {
                    home.put("pg", (int)home.get("pg") + 1);
                    away.put("pp", (int)away.get("pp") + 1);
                    home.put("pts", (int)home.get("pts") + 3);
                } else if (gh < ga) {
                    away.put("pg", (int)away.get("pg") + 1);
                    home.put("pp", (int)home.get("pp") + 1);
                    away.put("pts", (int)away.get("pts") + 3);
                } else {
                    home.put("pe", (int)home.get("pe") + 1);
                    away.put("pe", (int)away.get("pe") + 1);
                    home.put("pts", (int)home.get("pts") + 1);
                    away.put("pts", (int)away.get("pts") + 1);
                }
            }
        }
        // Ordenar por puntos, diferencia de gol, goles a favor
        List<Map<String, Object>> tablaList = new ArrayList<>(tabla.values());
        tablaList.sort((a, b) -> {
            int cmp = Integer.compare((int)b.get("pts"), (int)a.get("pts"));
            if (cmp != 0) return cmp;
            int difA = (int)a.get("gf") - (int)a.get("gc");
            int difB = (int)b.get("gf") - (int)b.get("gc");
            cmp = Integer.compare(difB, difA);
            if (cmp != 0) return cmp;
            return Integer.compare((int)b.get("gf"), (int)a.get("gf"));
        });
        return tablaList;
    }
    // PATCH: Sumar +1 victoria a los DTs ganadores de la jornada
    @PatchMapping("/game/{id}/dts")
    public Mono<ResponseEntity<Map<String, Object>>> updateDtsVictories(@PathVariable String id, @RequestBody List<String> ganadorIds) {
        return sessionRepo.findById(id)
            .flatMap(session -> {
                List<DirectorTecnico> dts = session.getDts();
                if (dts == null || ganadorIds == null) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "dts o ganadorIds nulos");
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
                }
                boolean updated = false;
                for (DirectorTecnico dt : dts) {
                    if (ganadorIds.contains(dt.getId())) {
                        int antes = dt.getVictorias();
                        dt.setVictorias(antes + 1);
                        logger.info("DT: {} (id={}) | Antes: {} | Nuevo: {}", dt.getNombre(), dt.getId(), antes, dt.getVictorias());
                        updated = true;
                    }
                }
                if (updated) session.setDts(dts);
                return sessionRepo.save(session).map(savedSession -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("session", savedSession);
                    result.put("dts", savedSession.getDts());
                    return ResponseEntity.ok(result);
                });
            })
            .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HashMap<>())));
    }
    // PATCH: Persistir resultados jugados en el fixture de la sesión
    @PatchMapping("/game/{id}/fixture")
    public Mono<ResponseEntity<?>> updateSessionFixture(@PathVariable String id, @RequestBody List<Map<String, Object>> playedMatches) {
        logger.info("[PATCH /game/{}/fixture] Persistiendo resultados jugados: {}", id, playedMatches != null ? playedMatches.size() : 0);
        return sessionRepo.findById(id)
            .flatMap(session -> {
                List<Fixture> fixture = session.getFixture();
                if (fixture == null || playedMatches == null) return Mono.just(ResponseEntity.badRequest().build());
                // Mapear por id para acceso rápido
                Map<String, Fixture> fixtureById = new HashMap<>();
                for (Fixture f : fixture) fixtureById.put(f.getId(), f);

                // 1. Actualizar solo los partidos recibidos que NO estén FINISHED
                Set<String> updatedIds = new HashSet<>();
                for (Map<String, Object> match : playedMatches) {
                    String matchId = (String) match.get("id");
                    if (matchId == null) continue;
                    Fixture old = fixtureById.get(matchId);
                    if (old == null) continue;
                    // Si ya está FINISHED, nunca sobrescribir
                    if ("FINISHED".equalsIgnoreCase(old.getStatus())) continue;
                    // Extraer matchResult completo si existe
                    Integer goalsHome = null;
                    Integer goalsAway = null;
                    if (match.get("matchResult") instanceof Map<?,?> mr) {
                        Object gh = mr.get("goalsHome");
                        Object ga = mr.get("goalsAway");
                        if (gh instanceof Integer) goalsHome = (Integer) gh;
                        if (ga instanceof Integer) goalsAway = (Integer) ga;
                    }
                    // Forzar status FINISHED si ambos goles no son null (incluye 0-0)
                    String status = (goalsHome != null && goalsAway != null) ? "FINISHED" : (match.get("status") instanceof String ? (String) match.get("status") : "SCHEDULED");
                    // Crear nuevo Fixture actualizado
                    Fixture updated = new Fixture(
                        old.getId(),
                        old.getHomeClubId(),
                        old.getAwayClubId(),
                        old.getScheduledAt(),
                        status,
                        goalsHome,
                        goalsAway,
                        old.getSeasonId(),
                        old.getMatchday()
                    );
                    fixtureById.put(matchId, updated);
                    updatedIds.add(matchId);
                }

                // 2. Reconstruir el fixture: para cada partido original, si fue actualizado usarlo, si no dejar el original
                List<Fixture> updatedFixture = new ArrayList<>();
                for (Fixture f : fixture) {
                    updatedFixture.add(fixtureById.get(f.getId()));
                }

                // LOG: imprimir goles y status de cada partido
                logger.info("[PATCH /game/{}/fixture] Fixture actualizado:");
                for (Fixture f : updatedFixture) {
                    logger.info("  {} vs {} | status={} | goalsHome={} | goalsAway={}", f.getHomeClubId(), f.getAwayClubId(), f.getStatus(), f.getGoalsHome(), f.getGoalsAway());
                }
                session.setFixture(updatedFixture);
                return sessionRepo.save(session).map(ResponseEntity::ok);
            })
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    // PATCH: Guardar campeón y último lugar en la sesión
    @PatchMapping("/game/{id}/final")
    public Mono<ResponseEntity<GameSession>> setFinalResults(@PathVariable String id, @RequestBody Map<String, String> payload) {
        String campeonClubId = payload.get("campeonClubId");
        String campeonDtId = payload.get("campeonDtId");
        String ultimoClubId = payload.get("ultimoClubId");
        String ultimoDtId = payload.get("ultimoDtId");
        return sessionRepo.findById(id)
            .flatMap(session -> {
                session.setCampeonClubId(campeonClubId);
                session.setCampeonDtId(campeonDtId);
                session.setUltimoClubId(ultimoClubId);
                session.setUltimoDtId(ultimoDtId);
                return sessionRepo.save(session).map(ResponseEntity::ok);
            })
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
