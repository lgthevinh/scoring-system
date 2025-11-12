import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SyncService } from '../../../core/services/sync.service';
import { MatchDetailDto } from '../../../core/models/match.model';

@Component({
  selector: 'app-blue-alliance',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './blue-alliance.html',
  styleUrl: './blue-alliance.css'
})
export class BlueAlliance implements OnInit {
  loading: WritableSignal<boolean> = signal(true);
  error: WritableSignal<string | null> = signal(null);
  current: WritableSignal<MatchDetailDto | null> = signal(null);

  constructor(private syncService: SyncService) {}

  ngOnInit(): void {
    this.syncService.syncPlayingMatches().subscribe({
      next: (matches) => {
        this.current.set(matches && matches.length ? matches[0] : null);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Failed to load current playing match.');
        this.loading.set(false);
      }
    });
  }

  matchLabel(): string {
    const m = this.current();
    return m ? `Match ${m.match.matchCode}` : '';
  }

  blueTeamLine(): string {
    const m = this.current();
    return m ? m.blueTeams.map(t => t.teamId).join(', ') : '';
  }
}
