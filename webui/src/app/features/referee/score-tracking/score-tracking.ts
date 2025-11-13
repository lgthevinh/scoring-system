import { Component, OnDestroy, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SyncService } from '../../../core/services/sync.service';
import { MatchDetailDto } from '../../../core/models/match.model';

type CounterKey =
  | 'robotParked'
  | 'robotHanged'
  | 'ballEntered'
  | 'minorFault'
  | 'majorFault';

@Component({
  selector: 'app-score-tracking',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './score-tracking.html',
  styleUrl: './score-tracking.css'
})
export class ScoreTracking implements OnInit, OnDestroy {
  color: 'red' | 'blue' = 'red';
  matchId = '';

  loading: WritableSignal<boolean> = signal(true);
  error: WritableSignal<string | null> = signal(null);
  match: WritableSignal<MatchDetailDto | null> = signal(null);

  // Single-phase counters (scaffold for your wiring)
  counters: Record<CounterKey, WritableSignal<number>> = {
    robotParked: signal(0),
    robotHanged: signal(0),
    ballEntered: signal(0),
    minorFault: signal(0),
    majorFault: signal(0)
  };

  private sub: any;

  constructor(private route: ActivatedRoute, private sync: SyncService) {}

  ngOnInit(): void {
    this.sub = this.route.paramMap.subscribe(params => {
      const colorParam = (params.get('color') || 'red').toLowerCase();
      this.color = (colorParam === 'blue' ? 'blue' : 'red');
      this.matchId = params.get('matchId') || '';

      this.fetchMatch();
    });
  }

  ngOnDestroy(): void {
    if (this.sub) this.sub.unsubscribe();
  }

  private fetchMatch() {
    this.loading.set(true);
    this.error.set(null);
    this.match.set(null);

    this.sync.syncPlayingMatches().subscribe({
      next: (list) => {
        const found = (list || []).find(m => m.match.id === this.matchId);
        this.match.set(found ?? null);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Failed to fetch playing matches.');
        this.loading.set(false);
      }
    });
  }

  titleText(): string {
    return `${this.matchId || '-' } score tracking`;
  }

  inc(key: CounterKey) { this.counters[key].set(this.counters[key]() + 1); }
  dec(key: CounterKey) { this.counters[key].set(Math.max(0, this.counters[key]() - 1)); }

  redTeamLine(): string {
    const m = this.match();
    return m ? m.redTeams.map(t => t.teamId).join(', ') : '';
  }

  blueTeamLine(): string {
    const m = this.match();
    return m ? m.blueTeams.map(t => t.teamId).join(', ') : '';
  }
}
