package com.cyberfoot.domain.model;

import java.time.Instant;

public record Fixture(
    String id,
    String homeClubId,
    String awayClubId,
    Instant scheduledAt,
    String status,
    Integer goalsHome,
    Integer goalsAway,
    String seasonId,
    int matchday
) {}
// TODO[M1.1]: Agregar atributos extra en M2
