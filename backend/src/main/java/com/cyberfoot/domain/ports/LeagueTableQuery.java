package com.cyberfoot.domain.ports;

import com.cyberfoot.domain.model.LeagueRow;
import reactor.core.publisher.Flux;
import java.util.UUID;

public interface LeagueTableQuery {
    Flux<LeagueRow> table(UUID seasonId);
}
