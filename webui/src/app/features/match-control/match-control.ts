import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  MatchService,
  MockMatchService,
  ProdMatchService
} from '../../core/services/match.service';
import { environment } from '../../../environments/environment';
import { MatchDetailDto } from '../../core/models/match.model';
import { ScorekeeperService } from '../../core/services/scorekeeper.service';
import {BroadcastService} from '../../core/services/broadcast.service';
import {SyncService} from '../../core/services/sync.service';

type TabKey =
  | 'schedule'
  | 'incomplete'
  | 'score-edit'
  | 'active-match'
  | 'settings'
  | 'alliance-selection'
  | 'video-switch'
  | 'present-awards'
  | 'help';

@Component({
  selector: 'app-match-control',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [
    {
      provide: MatchService,
      useClass: environment.useFakeData ? MockMatchService : ProdMatchService
    }
  ],
  templateUrl: './match-control.html',
  styleUrl: './match-control.css'
})
export class MatchControl implements OnInit {
  tabs: { key: TabKey; label: string; icon?: string }[] = [
    { key: 'schedule', label: 'Schedule', icon: 'bi-list-ul' },
    { key: 'incomplete', label: 'Incomplete Matches', icon: 'bi-exclamation-circle' },
    { key: 'score-edit', label: 'Score Edit', icon: 'bi-pencil-square' },
    { key: 'active-match', label: 'Active Match', icon: 'bi-lightning-charge' },
    { key: 'settings', label: 'Settings', icon: 'bi-gear' },
    { key: 'alliance-selection', label: 'Alliance Selection', icon: 'bi-people' },
    { key: 'video-switch', label: 'Video Switch', icon: 'bi-camera-video' },
    { key: 'present-awards', label: 'Present Awards', icon: 'bi-award' },
    { key: 'help', label: 'Help', icon: 'bi-question-circle' }
  ];

  selectedTab = signal<TabKey>('schedule');

  // Data
  schedule = signal<MatchDetailDto[]>([]);
  loaded = signal<MatchDetailDto | null>(null);
  active = signal<MatchDetailDto | null>(null);
  activeMatchTimer: WritableSignal<number | null> = signal<number | null>(null);

  constructor(
    private matchService: MatchService,
    private scorekeeper: ScorekeeperService,
    private broadcastService: BroadcastService,
    private syncService: SyncService
  ) {}

  ngOnInit(): void {
    this.loadSchedule(1);
    this.syncService.syncPlayingMatches().subscribe({
      next: (matches) => {
        console.log('Synced playing matches', matches);

        if (matches && matches.length > 0) {
          // Assume first match is active, second is loaded
          this.active.set(matches[0]);
          if (matches.length > 1) {
            this.loaded.set(matches[1]);
          } else {
            this.loaded.set(null);
          }
        }
      },
      error: (e) => console.error('Failed to sync playing matches', e.message)
    });
    this.broadcastService.subscribeToTopic("/topic/display/field/*/timer").subscribe({
      next: (msg) => {
        console.log("Received timer update:", msg);
        if (msg.payload && msg.payload.remainingSeconds !== undefined) {
          this.activeMatchTimer.set(msg.payload.remainingSeconds);
        }
      },
      error: (e) => console.error("Failed to subscribe to timer updates:", e)
    });
  }

  setTab(key: TabKey) {
    this.selectedTab.set(key);
  }

  // ---- Data loading ----
  loadSchedule(matchType: number) {
    this.matchService.getMatches(matchType).subscribe({
      next: (list) => this.schedule.set(list),
      error: (e) => console.error('Failed to load schedule', e)
    });
  }

  // ---- Schedule row actions ----
  playMatch(match: MatchDetailDto) {
    // Backend: set next match on the field
    this.scorekeeper.setNextMatch(match.match.id).subscribe({
      next: () => this.loaded.set(match),
      error: (e) => {
        console.error('Failed to set next match', e);
        // Fallback for UI in case backend not ready
        this.loaded.set(match);
      }
    });
  }

  enterScores(match: MatchDetailDto) {
    // TODO: Navigate or open a dialog to enter/override scores for match.match.id
    console.debug('Enter scores for match', match.match.id);
  }

  // ---- Top buttons (Loaded section) ----
  loadNextMatch() {
    const list = this.schedule();
    if (!list.length) return;
    const current = this.loaded();
    const idx = current ? list.findIndex(m => m.match.id === current.match.id) : -1;
    const next = list[(idx + 1 + list.length) % list.length];
    // Optimistic UI update, then backend (if any)
    this.scorekeeper.setNextMatch(next.match.id).subscribe({
      next: () => this.loaded.set(next),
      error: () => {
        console.error('Failed to set next match');
        alert('Failed to load next match');
      }
    });
  }

  showPreview() {
    // TODO: Integrate with display/preview system
    console.debug('Show preview clicked');
  }

  showMatch() {
    // TODO: Integrate with audience/field display
    console.debug('Show match clicked');
  }

  // ---- Top buttons (Active section) ----
  startMatch() {
    const toStart = this.loaded();
    if (!toStart) {
      console.warn('No loaded match to start.');
      return;
    }
    this.scorekeeper.startCurrentMatch().subscribe({
      next: () => {
        this.active.set(toStart);
        this.loaded.set(null); // Clear loaded match after starting
      },
      error: (e) => {
        console.error('Failed to start current match', e);
        // Fallback for UI
        this.active.set(toStart);
      }
    });

  }

  abortMatch() {
    // TODO: Implement backend abort endpoint if/when available
    console.debug('Abort match clicked');
  }

  commitAndPostLastMatch() {
    this.scorekeeper.commitFinalScore().subscribe({
      next: () => console.debug('Committed last match results'),
      error: (e) => console.error('Failed to commit last match', e)
    });
  }

  // ---- Labels and helpers for FTC-like header ----
  selectedTabTitle(): string {
    const t = this.tabs.find(x => x.key === this.selectedTab());
    return t?.label ?? '';
  }

  loadedTitle(): string {
    const m = this.loaded();
    return m ? `${m.match.matchCode}` : '-';
  }

  activeTitle(): string {
    const m = this.active();
    return m ? `${m.match.matchCode}` : '-';
  }

  loadedRed(): string {
    const m = this.loaded();
    return m ? m.redTeams.map(t => t.teamId).join(', ') : '-';
    // If you prefer team names: t.teamName
  }

  loadedBlue(): string {
    const m = this.loaded();
    return m ? m.blueTeams.map(t => t.teamId).join(', ') : '-';
  }

  activeRed(): string {
    const m = this.active();
    return m ? m.redTeams.map(t => t.teamId).join(', ') : '-';
  }

  activeBlue(): string {
    const m = this.active();
    return m ? m.blueTeams.map(t => t.teamId).join(', ') : '-';
  }

  loadedTime(): string {
    const m = this.loaded();
    return m ? this.formatLocalTime(m.match.matchStartTime) : '';
  }

  activeTime(): string {
    const m = this.active();
    return m ? this.formatLocalTime(m.match.matchStartTime) : '';
  }

  activeMatchTimerDisplay(): string {
    const seconds = this.activeMatchTimer();
    if (seconds === null) return '';
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  private formatLocalTime(iso: string | null | undefined): string {
    if (!iso) return '';
    try {
      const d = new Date(iso);
      return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch {
      return '';
    }
  }
}
