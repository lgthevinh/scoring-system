import { Component, OnDestroy, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SyncService } from '../../../core/services/sync.service';
import { MatchDetailDto } from '../../../core/models/match.model';
import {BroadcastService} from '../../../core/services/broadcast.service';

type CounterKey =
  | 'robotParked'
  | 'robotHanged'
  | 'ballEntered'
  | 'minorFault'
  | 'majorFault';

type UpdateReason = 'inc' | 'dec' | 'reset' | 'init';

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

  // Submission state (kept simple; wire later)
  submitting: WritableSignal<boolean> = signal(false);
  submitMessage: WritableSignal<string> = signal('');

  // Versioning + source for robust live updates
  private version: WritableSignal<number> = signal(0);
  private readonly sourceId: string = this.initSourceId();

  // Single-phase counters
  counters: Record<CounterKey, WritableSignal<number>> = {
    robotParked: signal(0),
    robotHanged: signal(0),
    ballEntered: signal(0),
    minorFault: signal(0),
    majorFault: signal(0)
  };

  private sub: any;

  constructor(
    private route: ActivatedRoute,
    private sync: SyncService,
    private broadcastService: BroadcastService
  ) {}

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
        // Send an initial snapshot so displays can align immediately
        this.onScoreUpdate('init', 'ballEntered', this.counters.ballEntered());
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

  inc(key: CounterKey) {
    this.counters[key].set(this.counters[key]() + 1);
    this.onScoreUpdate('inc', key, this.counters[key]());
  }

  dec(key: CounterKey) {
    this.counters[key].set(Math.max(0, this.counters[key]() - 1));
    this.onScoreUpdate('dec', key, this.counters[key]());
  }

  resetCounters() {
    (Object.keys(this.counters) as CounterKey[]).forEach(k => {
      this.counters[k].set(0);
      this.onScoreUpdate('reset', k, 0);
    });
  }

  redTeamLine(): string {
    const m = this.match();
    return m ? m.redTeams.map(t => t.teamId).join(', ') : '';
  }

  blueTeamLine(): string {
    const m = this.match();
    return m ? m.blueTeams.map(t => t.teamId).join(', ') : '';
  }

  // Placeholder submit method – replace with real API call later.
  submitScore() {
    if (!this.match()) {
      this.submitMessage.set('No match loaded – cannot submit.');
      setTimeout(() => this.submitMessage.set(''), 3000);
      return;
    }
    this.submitting.set(true);
    this.submitMessage.set('');

    const payload = this.buildFullSnapshot('init', 'ballEntered', this.counters.ballEntered());
    // TODO: Replace with backend submit (finalize) call
    console.log('[ScoreTracking] Submit placeholder payload:', payload);

    setTimeout(() => {
      this.submitting.set(false);
      this.submitMessage.set('Score submitted (placeholder). Implement backend.');
      setTimeout(() => this.submitMessage.set(''), 4000);
    }, 600);
  }

  /**
   * Re-implemented onScoreUpdate to send FULL SNAPSHOT each time.
   * This is more robust on local networks and with few referee devices.
   *
   * TODO: Publish snapshot over WebSocket (BroadcastService) or HTTP PATCH to your live-update endpoint.
   */
  onScoreUpdate(reason: UpdateReason, key: CounterKey, value: number) {
    const snapshot = this.buildFullSnapshot(reason, key, value);

    // TODO: Publish snapshot in realtime
    this.broadcastService.publishMessage(`/app/live/score/update/${this.color}`, snapshot);
    // Option A (WebSocket/STOMP):
    //   this.broadcastService.send('/app/live-score/update', snapshot);
    // Option B (HTTP PATCH):
    //   this.http.patch('/api/score/live-update', snapshot.payload).subscribe(...)
    // Option C (Server-Sent Events not needed for upstream).
    console.debug('[LIVE_SCORE_SNAPSHOT]', snapshot);
  }

  // Build a full, idempotent snapshot of current state (with version/source)
  private buildFullSnapshot(reason: UpdateReason, key: CounterKey, value: number) {
    const nextVersion = this.version() + 1;
    this.version.set(nextVersion);

    return {
      type: 'LIVE_SCORE_SNAPSHOT',
      payload: {
        matchId: this.matchId,
        alliance: this.color,
        version: nextVersion,
        sourceId: this.sourceId,
        at: new Date().toISOString(),
        state: {
          robotParked: this.counters.robotParked(),
          robotHanged: this.counters.robotHanged(),
          ballEntered: this.counters.ballEntered(),
          minorFault: this.counters.minorFault(),
          majorFault: this.counters.majorFault()
        },
        lastChange: { key, reason, value }
      }
    };
  }

  // Generate or retrieve a stable device id for auditing
  private initSourceId(): string {
    const storageKey = 'refDeviceId';
    let id = localStorage.getItem(storageKey);
    if (!id) {
      id = 'ref-' + Math.random().toString(36).slice(2, 8);
      localStorage.setItem(storageKey, id);
    }
    return id;
  }
}
