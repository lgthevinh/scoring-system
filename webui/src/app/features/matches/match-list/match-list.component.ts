import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatchService } from '../../../core/services/match.service';
import { MatchDetailDto } from '../../../core/models/match.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-match-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './match-list.component.html'
})
export class MatchListComponent implements OnInit {
  // Use WritableSignals for all component state
  matches: WritableSignal<MatchDetailDto[]> = signal([]);
  isLoading: WritableSignal<boolean> = signal(false);
  errorMessage: WritableSignal<string> = signal('');

  activeMatchType = 1; // Qualification
  showScheduleModal = false;

  scheduleConfig = {
    numberOfMatches: 50,
    startTime: new Date().toISOString().slice(0, 16),
    matchDuration: 15,
    timeBlocks: [
      { name: "Lunch", startTime: new Date(new Date().setHours(12, 0, 0, 0)).toISOString().slice(0, 16), duration: "60" }
    ]
  };

  matchTypes = [
    { id: 0, name: 'Practice' },
    { id: 1, name: 'Qualification' },
    { id: 2, name: 'Semifinals' },
    { id: 3, name: 'Finals' }
  ];

  constructor(private matchService: MatchService, private router: Router) { }

  ngOnInit() {
    this.loadMatches(this.activeMatchType);
  }

  loadMatches(type: number) {
    this.isLoading.set(true);
    this.errorMessage.set('');
    this.activeMatchType = type;
    this.matchService.getMatches(type).subscribe({
      next: (data) => {
        const sortedData = data.sort((a, b) => a.match.matchNumber - b.match.matchNumber);
        this.matches.set(sortedData);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load matches', err);
        this.errorMessage.set('Could not load matches. Please try again later.');
        this.isLoading.set(false);
      }
    });
  }

  goToScoring(matchId: string) {
    this.router.navigate(['/score', matchId]);
  }

  generateSchedule() {
    this.matchService.generateSchedule(this.scheduleConfig).subscribe(() => {
      this.loadMatches(this.activeMatchType);
      this.showScheduleModal = false;
    });
  }
}

