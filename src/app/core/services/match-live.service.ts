import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';


export interface MatchEvent {
  seq: number;
  minute: number;
  type: string;
  goalsHome: number;
  goalsAway: number;
  message: string;
}

export interface MatchState {
  isPlaying: boolean;
  events: MatchEvent[];
  matchProgress: number;
  matchResult: { finalResult: string; events: MatchEvent[] } | null;
}


@Injectable({ providedIn: 'root' })
export class MatchLiveService {
  private matchState$ = new BehaviorSubject<MatchState>({
    isPlaying: false,
    events: [],
    matchProgress: 0,
    matchResult: null
  });

  getState(): Observable<MatchState> {
    return this.matchState$.asObservable();
  }

  startSimulation(fixtureId: string, teamId: string, starters: string[]): void {
    console.log('[MatchLiveService] Iniciando simulación:', { fixtureId, teamId, starters });
    this.matchState$.next({
      isPlaying: true,
      events: [],
      matchProgress: 0,
      matchResult: null
    });

    fetch(`/api/match/${fixtureId}/simulate`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ teamId, starters })
    }).then(() => {
      console.log('[MatchLiveService] Esperando eventos SSE en:', `/api/match/${fixtureId}/events`);
      const es = new EventSource(`/api/match/${fixtureId}/events`);
      es.onmessage = ev => {
        const event = JSON.parse(ev.data);
        console.log('[MatchLiveService] Evento SSE recibido:', event);
        const prev = this.matchState$.getValue();
        const newEvents = [...prev.events, event];
        const newProgress = Math.floor((event.minute / 90) * 100);
        let matchResult = prev.matchResult;
        let isPlaying = true;
        if (event.type === 'END') {
          isPlaying = false;
          matchResult = {
            finalResult: event.message,
            events: newEvents
          };
          es.close();
          console.log('[MatchLiveService] Partido finalizado.');
        }
        this.matchState$.next({
          isPlaying,
          events: newEvents,
          matchProgress: newProgress,
          matchResult
        });
      };
      es.onerror = err => {
        console.error('[MatchLiveService] Error SSE:', err);
        es.close();
        this.matchState$.next({
          ...this.matchState$.getValue(),
          isPlaying: false
        });
      };
    }).catch(err => {
      console.error('[MatchLiveService] Error iniciando simulación:', err);
    });
  }

  reset(): void {
    this.matchState$.next({
      isPlaying: false,
      events: [],
      matchProgress: 0,
      matchResult: null
    });
  }
}
