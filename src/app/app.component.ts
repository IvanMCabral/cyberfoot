


import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { MatchLiveService } from './core/services/match-live.service';
import { TeamService, Team } from './core/services/team.service';
import { FixtureService, Fixture } from './core/services/fixture.service';
import { StandingsService, StandingsRow } from './core/services/standings.service';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { TeamLineupComponent } from './components/team-lineup/team-lineup.component';
import { FixtureListComponent } from './components/fixture-list/fixture-list.component';
import { MatchResultComponent } from './components/match-result/match-result.component';
import { FixtureRoundComponent } from './components/fixture-round/fixture-round.component';
import { StandingsTableComponent } from './components/standings-table/standings-table.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterOutlet, HttpClientModule, TeamLineupComponent, FixtureListComponent, MatchResultComponent, FixtureRoundComponent, StandingsTableComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  showLineup: boolean = true;
  showLineupKey: number = 0;
  title(title: any) {
    throw new Error('Method not implemented.');
  }
  constructor(
    private matchLive: MatchLiveService,
    private teamService: TeamService,
    private fixtureService: FixtureService,
    private standingsService: StandingsService,
    private cdr: ChangeDetectorRef
  ) {}
  standings: StandingsRow[] = [];
  teams: Team[] = [];
  selectedTeamIndex: number | null = null;
  rounds: Fixture[][] = [];
  selectedStarters: any[] = [];
  step: number = 1;
  matchInterval: any = null;
  currentRoundIndex: number = 0;

  ngOnInit() {
    this.teamService.getTeams().subscribe(teams => {
      this.teams = teams;
    });
  }

  selectTeam(idx: number) {
    this.selectedTeamIndex = idx;
    this.fixtureService.createFullFixture().subscribe((rounds: Fixture[][]) => {
      this.rounds = rounds;
      this.standings = this.standingsService.calculate(this.rounds, this.teams);
      this.fixtureService.setCurrentMatchIndex(0);
      this.fixtureService.setStep(2);
      this.step = this.fixtureService.getStep();
    });
  }

  onLineupConfirmed(team: any) {
    console.log('[onLineupConfirmed] Confirmando titulares:', team.players.filter((p: any) => p.isStarter));
    this.teamService.setStarters(team.id, team.players.filter((p: any) => p.isStarter));
    this.selectedStarters = this.teamService.getStarters(team.id);
    console.log('[onLineupConfirmed] selectedStarters:', this.selectedStarters);
    // Avanzar al siguiente partido/ronda
    if (this.currentRoundIndex < this.rounds.length) {
      this.fixtureService.setStep(3);
      this.step = this.fixtureService.getStep();
      this.cdr.detectChanges();
      console.log('[onLineupConfirmed] step:', this.step);
    } else {
      // Si ya no hay más rondas, avanzar al paso final
      this.fixtureService.setStep(4);
      this.step = this.fixtureService.getStep();
      this.cdr.detectChanges();
      console.log('[onLineupConfirmed] step:', this.step);
    }
  }

  onNextRound() {
  console.log('[onNextRound] currentRoundIndex before:', this.currentRoundIndex);
  console.log('[onNextRound] step before:', this.step);
  console.log('[onNextRound] selectedTeamIndex before:', this.selectedTeamIndex);
  if (this.currentRoundIndex < this.rounds.length - 1) {
      this.currentRoundIndex++;
      console.log('[onNextRound] currentRoundIndex after:', this.currentRoundIndex);
      // Volver a pantalla de selección de equipo y titulares
     // Reiniciar titulares en el equipo seleccionado y limpiar starters en el servicio ANTES de mostrar la pantalla
     if (this.selectedTeamIndex !== null && this.teams[this.selectedTeamIndex]) {
       const oldTeam = this.teams[this.selectedTeamIndex];
       // Crear nuevo array de jugadores y nuevo objeto Team
       const newPlayers = oldTeam.players.map(p => ({ ...p, isStarter: false }));
       const newTeam: Team = {
         id: oldTeam.id,
         name: oldTeam.name,
         overall: oldTeam.overall,
         players: newPlayers
       };
       this.teams = this.teams.map((t, i) => i === this.selectedTeamIndex ? newTeam : t);
       // Limpiar titulares en el servicio y en el estado local
       this.teamService.setStarters(newTeam.id, []);
       this.selectedStarters = [];
       this.showLineupKey = this.currentRoundIndex;
       console.log('[onNextRound] Reiniciando titulares:', newTeam.players);
      }
     this.fixtureService.setStep(2);
     this.step = 2;
     this.showLineup = false;
     this.cdr.detectChanges();
     setTimeout(() => {
       this.showLineup = true;
       this.cdr.detectChanges();
     }, 0);
    } else {
      // Calcular campeón y avanzar el paso final
      this.fixtureService.setChampion(this.teams[this.selectedTeamIndex!]);
      this.fixtureService.setStep(4);
      this.step = this.fixtureService.getStep();
      this.cdr.detectChanges();
    }
    console.log('[onNextRound] step:', this.step);
  }

  onRoundUpdated(updatedRound: Fixture[]) {
    this.rounds[this.currentRoundIndex] = updatedRound;
    this.standings = this.standingsService.calculate(this.rounds, this.teams);
  }
}