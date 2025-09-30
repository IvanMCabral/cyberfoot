import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

export interface Player {
  name: string;
  rating: number;
  isStarter?: boolean;
  uid?: string;
}

export interface Team {
  id: string;
  name: string;
  overall: number;
  players: Player[];
}

@Injectable({ providedIn: 'root' })
export class TeamService {
  private startersMap: { [teamId: string]: Player[] } = {};

  constructor(private http: HttpClient) {}

  getTeams(): Observable<Team[]> {
    return this.http.get<any[]>('/api/clubs').pipe(
      map(clubs => clubs || []),
      switchMap(clubs =>
        forkJoin(
          clubs.map(club =>
            this.http.get<any[]>(`/api/players?clubId=${club._id || club.id}`).pipe(
              map(players => players.map(p => ({ ...p, isStarter: false })))
            )
          )
        ).pipe(
          map(playersByClub =>
            clubs.map((club, idx) => {
              const players = playersByClub[idx];
              const overall = players.length > 0 ? Math.round(players.reduce((sum, p) => sum + (p.rating || 0), 0) / players.length) : 0;
              return { id: club.id || club._id, name: club.name, overall, players };
            })
          )
        )
      )
    );
  }

  setStarters(teamId: string, starters: Player[]): void {
    this.startersMap[teamId] = starters;
  }

  getStarters(teamId: string): Player[] {
    return this.startersMap[teamId] || [];
  }
}
