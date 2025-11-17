import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatchDetailDto } from '../../core/models/match.model';
import {MatchService, MockMatchService, ProdMatchService} from '../../core/services/match.service';
import {environment} from '../../../environments/environment';

@Component({
  selector: 'app-match-results',
  standalone: true,
  imports: [CommonModule],
  providers: [
    {
      provide: MatchService,
      useClass: environment.useFakeData ? MockMatchService : ProdMatchService
    }
  ],
  templateUrl: './match-results.html',
  styleUrl: './match-results.css'
})
export class MatchResults implements OnInit {
  loading: WritableSignal<boolean> = signal(true);
  error: WritableSignal<string | null> = signal(null);
  matches: WritableSignal<MatchDetailDto[]> = signal([]);

  // UI-only toggles (keep backend/data code untouched)
  condensed = false;
  keepBackgroundOnPrint = false;

  constructor(private matchService: MatchService) {}

  ngOnInit(): void {
    this.fetchMatchResults();
  }

  fetchMatchResults() {
    this.loading.set(true);
    this.error.set(null);

    // keep existing backend/data handling via MatchService
    this.matchService.getMatches(1, true).subscribe({
      next: (matchResults) => {
        this.matches.set(matchResults || []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Failed to fetch match results.');
        this.loading.set(false);
      },
    });
  }

  // Text for result cell, e.g., "28-12 R"
  formatResult(match: MatchDetailDto): string {
    const redScore = match?.redScore?.totalScore ?? 0;
    const blueScore = match?.blueScore?.totalScore ?? 0;
    if (redScore > blueScore) return `${redScore} - ${blueScore} R`;
    if (blueScore > redScore) return `${redScore} - ${blueScore} B`;
    return `${redScore}-${blueScore}`;
  }

  // Unplayed = no start time (kept logic)
  isUnplayed(match: MatchDetailDto): boolean {
    return !match?.match?.matchEndTime;
  }

  // Winner side for coloring the result tile
  winner(match: MatchDetailDto): 'red' | 'blue' | null {
    if (this.isUnplayed(match)) return null;
    const redScore = match?.redScore?.totalScore ?? 0;
    const blueScore = match?.blueScore?.totalScore ?? 0;
    if (redScore > blueScore) return 'red';
    if (blueScore > redScore) return 'blue';
    return null; // tie or equal
  }

  // Helper to render team IDs list elsewhere if needed
  getTeamIds(teamList: any[]): string {
    return (teamList || []).map(team => team.teamId).join(', ');
  }

  // Row helpers to render multi-line team lists (FTC-like layout)
  teamRowCount(match: MatchDetailDto): number {
    const r = match?.redTeams?.length ?? 0;
    const b = match?.blueTeams?.length ?? 0;
    return Math.max(r, b, 1);
  }

  rows(n: number): number[] {
    return Array.from({ length: n }, (_, i) => i);
  }

  teamIdAt(teams: any[] | undefined, index: number): string {
    if (!teams || index >= teams.length) return '';
    return teams[index]?.teamId ?? '';
  }

  printPage() {
    window.print();
  }
}
