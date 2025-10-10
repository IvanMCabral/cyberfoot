package com.cyberfoot.domain.model;

import java.util.List;

public record Club(
    String id,
    String name,
    int overall,
    String directorTecnicoId,
    List<Player> players
) {}
// TODO[M1.1]: Agregar atributos extra en M2
