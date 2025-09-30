import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'standings-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './standings-table.component.html',
  styleUrls: ['./standings-table.component.scss']
})
export class StandingsTableComponent {
  @Input() standings: any[] = [];
}
