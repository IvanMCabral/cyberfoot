import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'team-lineup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './team-lineup.component.html',
  styleUrls: ['./team-lineup.component.scss']
})
export class TeamLineupComponent {
  @Input() team: any;
  @Output() lineupConfirmed = new EventEmitter<any>();

  getStartersCount(): number {
    return this.team.players.filter((p: any) => p.isStarter).length;
  }

  confirm() {
    this.lineupConfirmed.emit(this.team);
  }
}
