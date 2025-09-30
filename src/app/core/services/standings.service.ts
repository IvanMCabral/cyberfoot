import { Injectable } from '@angular/core';
import { Fixture } from './fixture.service';
import { Team } from './team.service';

export interface StandingsRow {
  id: string;
  name: string;
  played: number;
  won: number;
  drawn: number;
  lost: number;
  goalsFor: number;
  goalsAgainst: number;
  points: number;
}

@Injectable({ providedIn: 'root' })
export class StandingsService {
  calculate(rounds: Fixture[][], teams: Team[]): StandingsRow[] {
    const table: { [id: string]: StandingsRow } = {};
    teams.forEach(team => {
      table[team.id] = {
        id: team.id,
        name: team.name,
        played: 0,
        won: 0,
        drawn: 0,
        lost: 0,
        goalsFor: 0,
        goalsAgainst: 0,
        points: 0
      };
    });
    rounds.forEach(round => {
      round.forEach(match => {
        const home = table[match.homeClubId];
        const away = table[match.awayClubId];
        if (!home || !away || !match.matchResult) return;
        home.played++;
        away.played++;
        const goalsHome = match.matchResult.goalsHome;
        const goalsAway = match.matchResult.goalsAway;
        home.goalsFor += goalsHome;
        home.goalsAgainst += goalsAway;
        away.goalsFor += goalsAway;
        away.goalsAgainst += goalsHome;
        if (goalsHome > goalsAway) {
          home.won++;
          away.lost++;
          home.points += 3;
        } else if (goalsHome < goalsAway) {
          away.won++;
          home.lost++;
          away.points += 3;
        } else {
          home.drawn++;
          away.drawn++;
          home.points++;
          away.points++;
        }
      });
    });
    return Object.values(table).sort((a, b) => b.points - a.points || b.goalsFor - a.goalsFor);
  }
}
