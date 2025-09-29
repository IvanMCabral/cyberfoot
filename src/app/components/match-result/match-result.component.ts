import { Component, Output, EventEmitter, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatchLiveService, MatchState } from '../../core/services/match-live.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'match-result',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './match-result.component.html',
  styleUrls: ['./match-result.component.scss']
})
export class MatchResultComponent implements OnInit, OnDestroy {
  isPlaying: boolean = false;
  matchResult: { finalResult: string; events: any[] } | null = null;
  animatedEvents: any[] = [];
  matchProgress: number = 0;
  @Output() nextMatch = new EventEmitter<void>();

  private sub: Subscription | null = null;

  constructor(private matchLive: MatchLiveService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    console.log('[MatchResultComponent] Suscribiendo al estado del partido...');
    this.sub = this.matchLive.getState().subscribe((state: MatchState) => {
      this.isPlaying = state.isPlaying;
      this.matchResult = state.matchResult;
      // Clonar el array para asegurar nueva referencia
      this.animatedEvents = [...state.events];
      this.matchProgress = state.matchProgress;
      console.log('[MatchResultComponent] Estado actualizado:', state);
      this.cdr.detectChanges();
    });
  }

  ngOnDestroy() {
    if (this.sub) this.sub.unsubscribe();
  }

  trackBySeq(index: number, event: any) {
    return event.seq || index;
  }
}
