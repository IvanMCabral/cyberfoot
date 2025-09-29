
import { Component, OnInit } from '@angular/core';
import { MatchLiveService } from './core/services/match-live.service';
import { TeamService, Team } from './core/services/team.service';
import { FixtureService, Fixture } from './core/services/fixture.service';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { TeamLineupComponent } from './components/team-lineup/team-lineup.component';
import { FixtureListComponent } from './components/fixture-list/fixture-list.component';
import { MatchResultComponent } from './components/match-result/match-result.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterOutlet, HttpClientModule, TeamLineupComponent, FixtureListComponent, MatchResultComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
  export class AppComponent implements OnInit {
  title(title: any) {
    throw new Error('Method not implemented.');
  }
  constructor(
    private matchLive: MatchLiveService,
    private teamService: TeamService,
    private fixtureService: FixtureService
  ) {}
  teams: Team[] = [];
  selectedTeamIndex: number | null = null;
  fixtures: Fixture[] = [];
  selectedStarters: any[] = [];
  step: number = 1;
  matchInterval: any = null;

  ngOnInit() {
    this.teamService.getTeams().subscribe(teams => {
      this.teams = teams;
    });
  }

  selectTeam(idx: number) {
    this.selectedTeamIndex = idx;
    const team = this.teams[idx];
    this.fixtureService.createFixtures((team as any).id).subscribe(fixtures => {
      this.fixtures = fixtures;
      this.fixtureService.setCurrentMatchIndex(0);
      this.fixtureService.setStep(2);
      this.step = this.fixtureService.getStep();
    });
  }


  onLineupConfirmed(team: any) {
    this.teamService.setStarters(team.id, team.players.filter((p: any) => p.isStarter));
    this.selectedStarters = this.teamService.getStarters(team.id);
    this.fixtureService.setCurrentMatchIndex(0);
    this.fixtureService.setStep(3);
    this.step = this.fixtureService.getStep();
  }

  playCurrentMatch() {
    const currentMatchIndex = this.fixtureService.getCurrentMatchIndex();
    if (currentMatchIndex < this.fixtures.length) {
      const fixture = this.fixtures[currentMatchIndex];
      const starters = this.selectedStarters.map((p: any) => p.name);
      this.matchLive.startSimulation(fixture.id, this.teams[this.selectedTeamIndex!].id, starters);
    }
  }

  nextMatch() {
    if (this.matchInterval) {
      clearInterval(this.matchInterval);
    }
    this.matchLive.reset();
    const currentMatchIndex = this.fixtureService.getCurrentMatchIndex();
    this.fixtureService.addMatchToHistory(this.fixtures[currentMatchIndex]);
    if (currentMatchIndex < this.fixtures.length - 1) {
      this.fixtureService.setCurrentMatchIndex(currentMatchIndex + 1);
    } else {
      // Calcular campeón y avanzar el paso final
      this.fixtureService.setChampion(this.teams[this.selectedTeamIndex!]);
      this.fixtureService.setStep(4);
      this.step = this.fixtureService.getStep();
    }
  }

    // ...existing code...
}