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

  constructor(private matchService: MatchService) {
  }

  ngOnInit(): void {
    this.fetchMatchResults();
  }

  fetchMatchResults() {
    this.loading.set(true);
    this.error.set(null);

    this.matchService.getMatches(1, true).subscribe({
      next: (matchResults) => {
        this.matches.set(matchResults || []);
        this.loading.set(false);

        console.log('Fetched match results:', matchResults);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Failed to fetch match results.');
        this.loading.set(false);
      },
    });

  }

  formatResult(match: MatchDetailDto): string {
    // Example: format scores and indicate winning alliance
    const redScore = match?.redScore?.totalScore ?? 0;
    const blueScore = match?.blueScore?.totalScore ?? 0;
    if (redScore > blueScore) return `${redScore}-${blueScore} R`;
    if (blueScore > redScore) return `${redScore}-${blueScore} B`;
    return `${redScore}-${blueScore}`;
  }

  isUnplayed(match: MatchDetailDto): boolean {
    return !match?.match?.matchStartTime;
  }

  getTeamIds(teamList: any[]): string {
    return teamList.map(team => team.teamId).join(', ');
  }
}
