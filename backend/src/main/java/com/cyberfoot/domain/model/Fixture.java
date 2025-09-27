package com.cyberfoot.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Fixture(
    UUID id,
    UUID homeClubId,
    UUID awayClubId,
    Instant scheduledAt,
    String status,
    Integer goalsHome,
    Integer goalsAway
) {}
// TODO[M1.1]: Agregar atributos extra en M2
