import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SyncService } from '../../../../core/services/sync.service';
import { MatchDetailDto } from '../../../../core/models/match.model';

@Component({
  selector: 'app-blue-alliance',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './blue-alliance.html',
  styleUrl: './blue-alliance.css'
})
export class BlueAlliance implements OnInit {
  loading: WritableSignal<boolean> = signal(true);
  error: WritableSignal<string | null> = signal(null);
  matches: WritableSignal<MatchDetailDto[]> = signal([]);

  constructor(private syncService: SyncService) {}

  ngOnInit(): void {
    this.syncService.syncPlayingMatches().subscribe({
      next: (list) => {
        this.matches.set(list || []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Failed to load playing matches.');
        this.loading.set(false);
      }
    });
  }

  blueTeamLine(m: MatchDetailDto): string {
    return m.blueTeams.map(t => t.teamId).join(', ');
  }
}
