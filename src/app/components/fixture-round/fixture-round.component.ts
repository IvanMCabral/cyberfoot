// Eliminado: ngOnInit fuera de la clase
// Eliminado: función duplicada fuera de la clase
import { Component, Input, Output, EventEmitter, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Fixture } from '../../core/services/fixture.service';
import { MatchLiveService, MatchState } from '../../core/services/match-live.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'fixture-round',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './fixture-round.component.html',
  styleUrls: ['./fixture-round.component.scss']
})
export class FixtureRoundComponent implements OnInit {
  onContinue() {
    // Aquí deberías navegar a la página de selección de equipo
    // Por ejemplo, usando un router: this.router.navigate(['/seleccion-equipo']);
    // Por ahora, emitimos el evento nextRound para mantener el flujo
    this.nextRound.emit();
  }
  isSimulating(): boolean {
    return this.matchStates.some(s => s.isPlaying);
  }
  getGlobalProgress(): number {
    if (!this.matchStates || !this.matchStates.length) return 0;
    const sum = this.matchStates.reduce((acc: number, s: MatchState) => acc + (s.matchProgress || 0), 0);
    return Math.floor(sum / this.matchStates.length);
  }
  @Input() round: Fixture[] = [];
  @Input() teams: any[] = [];
  @Input() roundIndex: number = 0;
  @Input() selectedStarters: any[] = [];
  @Output() nextRound = new EventEmitter<void>();
  @Output() roundUpdated = new EventEmitter<Fixture[]>();

  matchStates: MatchState[] = [];
  eventSources: EventSource[] = [];
  allFinished: boolean = false;

  ngOnInit(): void {
    this.matchStates = Array.isArray(this.round) ? this.round.map(() => ({
      isPlaying: false,
      events: [],
      matchProgress: 0,
      matchResult: null
    })) : [];
  }

  constructor(private cdr: ChangeDetectorRef) {}

  getClubName(clubId: string): string {
    const club = this.teams?.find((t: any) => t.id === clubId);
    return club ? club.name : clubId;
  }

  playAllMatches() {
    this.matchStates = this.round.map(() => ({
      isPlaying: true,
      events: [],
      matchProgress: 0,
      matchResult: null
    }));
    this.allFinished = false;
    this.eventSources.forEach(es => es.close());
    this.eventSources = [];
    this.round.forEach((fixture, idx) => {
      fetch(`/api/match/${fixture.id}/simulate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ teamId: fixture.homeClubId, starters: this.selectedStarters.map((p: any) => p.name) })
      }).then(() => {
        const es = new EventSource(`/api/match/${fixture.id}/events`);
        this.eventSources.push(es);
        es.onmessage = ev => {
          const event = JSON.parse(ev.data);
          const prev = this.matchStates[idx];
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
          }
          this.matchStates[idx] = {
            isPlaying,
            events: newEvents,
            matchProgress: newProgress,
            matchResult
          };
          // Actualizar el resultado del partido en el fixture
          this.round[idx].matchResult = {
            goalsHome: event.goalsHome ?? 0,
            goalsAway: event.goalsAway ?? 0,
            ...matchResult
          };
          this.roundUpdated.emit(this.round);
          this.cdr.detectChanges();
          if (this.matchStates.every(s => s.matchResult)) {
            this.allFinished = true;
            this.cdr.detectChanges();
          }
        };
        es.onerror = err => {
          es.close();
          this.matchStates[idx] = {
            ...this.matchStates[idx],
            isPlaying: false
          };
          this.cdr.detectChanges();
        };
      });
    });
  }

  onNextRound() {
    this.eventSources.forEach(es => es.close());
    this.eventSources = [];
    this.matchStates = [];
    this.allFinished = false;
    this.nextRound.emit();
  }

  ngOnDestroy() {
    this.eventSources.forEach(es => es.close());
  }
}
