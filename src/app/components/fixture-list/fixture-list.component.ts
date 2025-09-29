import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'fixture-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './fixture-list.component.html',
  styleUrls: ['./fixture-list.component.scss']
})
export class FixtureListComponent {
  @Input() fixtures: any[] = [];
  @Input() teams: any[] = [];

  getClubName(clubId: string): string {
    const club = this.teams.find((t: any) => t.id === clubId);
    return club ? club.name : clubId;
  }
}
