import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SyncService } from '../../../../core/services/sync.service';
import { MatchDetailDto } from '../../../../core/models/match.model';

@Component({
  selector: 'app-red-alliance',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './red-alliance.html',
  styleUrl: './red-alliance.css'
})
export class RedAlliance implements OnInit {
  loading: WritableSignal<boolean> = signal(true);
  error: WritableSignal<string | null> = signal(null);
  matches: WritableSignal<MatchDetailDto[]> = signal([]);

  constructor(private syncService: SyncService) {}

  ngOnInit(): void {
    this.syncService.syncPlayingMatches().subscribe({
      next: (list) => {
        this.matches.set(list || []);
        this.loading.set(false);
        console.log('RedAlliance playing matches:', list);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Failed to load playing matches.');
        this.loading.set(false);
      }
    });
  }

  redTeamLine(m: MatchDetailDto): string {
    return m.redTeams.map(t => t.teamId).join(', ');
  }
}
