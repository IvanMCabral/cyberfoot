import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Fixture {
  id: string;
  homeClubId: string;
  homeClubName?: string;
  awayClubId: string;
  awayClubName?: string;
  matchday?: number;
  matchResult?: {
    goalsHome: number;
    goalsAway: number;
    finalResult?: string;
    events?: any[];
  };
}

@Injectable({ providedIn: 'root' })
export class FixtureService {
  private currentMatchIndex = 0;
  private step = 1;
  private matchHistory: Fixture[] = [];
  private champion: any = null;
  setChampion(team: any): void {
    this.champion = team;
  }

  getChampion(): any {
    return this.champion;
  }

  setStep(step: number): void {
    this.step = step;
  }

  getStep(): number {
    return this.step;
  }

  addMatchToHistory(fixture: Fixture): void {
    this.matchHistory.push(fixture);
  }

  getMatchHistory(): Fixture[] {
    return this.matchHistory;
  }

  constructor(private http: HttpClient) {}

  createFullFixture(): Observable<Fixture[][]> {
    return this.http.post<any>('/api/fixtures', {}).pipe(
      map(data => data.rounds as Fixture[][])
    );
  }

  setCurrentMatchIndex(idx: number): void {
    this.currentMatchIndex = idx;
  }

  getCurrentMatchIndex(): number {
    return this.currentMatchIndex;
  }
}
