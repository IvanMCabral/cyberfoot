import { Injectable } from '@angular/core';

export interface Player {
  name: string;
  score: number;
}

export interface Team {
  name: string;
  players: Player[];
}

export interface MatchResult {
  minute: number;
  event: string;
}

export interface MatchOutcome {
  results: MatchResult[];
  finalResult: string;
}

@Injectable({ providedIn: 'root' })
export class MatchEngineService {
  simulateMatch(teamA: Team, teamB: Team): MatchOutcome {
    const avgA = teamA.players.reduce((sum, p) => sum + p.score, 0) / teamA.players.length;
    const avgB = teamB.players.reduce((sum, p) => sum + p.score, 0) / teamB.players.length;
    let scoreA = 0;
    let scoreB = 0;
    const maxEvents = 10;
    const totalEvents = Math.floor(Math.random() * (maxEvents + 1));
    const results: MatchResult[] = [];
    for (let i = 1; i <= totalEvents; i++) {
      const minute = Math.floor((i / maxEvents) * 90);
      const probA = avgA / (avgA + avgB);
      const probB = avgB / (avgA + avgB);
      const rand = Math.random();
      if (rand < probA) {
        scoreA++;
        results.push({ minute, event: `${teamA.name} marca un gol! (${scoreA}-${scoreB})` });
      } else if (rand < probA + probB) {
        scoreB++;
        results.push({ minute, event: `${teamB.name} marca un gol! (${scoreA}-${scoreB})` });
      } else {
        results.push({ minute, event: `Minuto ${minute}: Sin goles.` });
      }
    }
    let finalResult: string;
    if (scoreA > scoreB) {
      finalResult = `¡${teamA.name} gana ${scoreA} - ${scoreB}!`;
    } else if (scoreB > scoreA) {
      finalResult = `¡${teamB.name} gana ${scoreB} - ${scoreA}!`;
    } else {
      finalResult = `¡Empate ${scoreA} - ${scoreB}!`;
    }
    return { results, finalResult };
  }
}
