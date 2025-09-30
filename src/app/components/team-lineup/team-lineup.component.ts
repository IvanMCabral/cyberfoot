import { Component, Input, Output, EventEmitter, OnInit, OnChanges, DoCheck, ChangeDetectorRef } from '@angular/core';
import { Player } from '../../core/services/team.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'team-lineup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './team-lineup.component.html',
  styleUrls: ['./team-lineup.component.scss']
})
export class TeamLineupComponent implements OnInit, OnChanges, DoCheck {
  constructor(private cdr: ChangeDetectorRef) {}
  ngDoCheck() {
    this.updateStartersCount();
  }
  @Input() team: any;
  @Output() lineupConfirmed = new EventEmitter<any>();
  startersCount: number = 0;

  ngOnInit() {
    this.updateStartersCount();
  }

  ngOnChanges() {
    this.updateStartersCount();
  }

  trackByPlayerName(index: number, player: Player): string {
    return player.name;
  }

  updateStartersCount() {
    this.startersCount = this.team?.players?.filter((p: any) => p.isStarter).length || 0;
    console.log('[team-lineup] startersCount:', this.startersCount, this.team?.players);
    this.cdr.detectChanges();
  }


  toggleStarter(event: any, player: any) {
    player.isStarter = event.target.checked;
    this.updateStartersCount();
  }

  confirm() {
    this.lineupConfirmed.emit(this.team);
  }
}
