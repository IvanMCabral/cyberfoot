package com.cyberfoot.domain.model;

import java.util.UUID;

public record Club(
    UUID id,
    String name,
    int overall
) {}
// TODO[M1.1]: Agregar atributos extra en M2
