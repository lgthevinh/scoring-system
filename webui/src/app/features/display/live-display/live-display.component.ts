import { Component, OnInit, OnDestroy, signal, WritableSignal, ChangeDetectorRef, computed } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Subscription } from 'rxjs';
import { LiveScoreService } from '../../../core/services/livescore.service';
import { Score } from '../../../core/models/score.model';
import { MatchService } from '../../../core/services/match.service';
import { MatchDetailDto } from '../../../core/models/match.model';
import { BroadcastMessage } from '../../../core/models/broadcast.model';

interface TimerState {
  matchId: string;
  remainingSeconds: number;
  state?: string;
  period?: string;
}

@Component({
  selector: 'app-live-display',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './live-display.component.html',
  styleUrls: ['./live-display.component.css']
})
export class LiveDisplayComponent implements OnInit, OnDestroy {
  currentMatch: WritableSignal<MatchDetailDto | null> = signal(null);
  redScore: WritableSignal<Score | null> = signal(null);
  blueScore: WritableSignal<Score | null> = signal(null);
  timer: WritableSignal<TimerState | null> = signal(null);
  isLoading: WritableSignal<boolean> = signal(true);
  errorMessage: WritableSignal<string> = signal('');

  formattedTime = computed(() => { /* ... unchanged ... */ });

  private messagesSubscription: Subscription | null = null;
  private currentMatchId: string = 'Q1';

  constructor(
    private liveScoreService: LiveScoreService,
    private matchService: MatchService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadInitialData(this.currentMatchId);
    this.subscribeToUpdates();
  }

  ngOnDestroy() {
    this.messagesSubscription?.unsubscribe();
  }

  loadInitialData(matchId: string) {
    // ... logic remains similar, but uses the updated service ...
    this.isLoading.set(true);
    this.errorMessage.set('');
    this.matchService.getMatches(1).subscribe({ // Assuming type 1 for Qualification
      next: (matches) => {
        const matchDetail = matches.find(m => m.match.id === matchId);
        if (matchDetail) {
          this.currentMatch.set(matchDetail);
          // Load initial scores using the updated service
          this.loadInitialScore(matchId + '_R');
          this.loadInitialScore(matchId + '_B');
        } else {
          this.errorMessage.set(`Match ${matchId} not found.`);
        }
        this.isLoading.set(false);
      },
      // ... error handling ...
    });
  }

  loadInitialScore(allianceId: string) {
    // Service now returns Observable<Score>
    this.matchService.getScore(allianceId).subscribe({
      next: (score) => {
        if (score) {
          this.updateScoreSignal(score);
        }
      },
      // ... error handling ...
    });
  }


  subscribeToUpdates() {
    // Use the generic messages$ observable from BroadcastService
    this.messagesSubscription = this.liveScoreService.messages$.subscribe(
      (message: BroadcastMessage) => {
        // Process based on message type
        switch (message.type) {
          case 'SCORE_UPDATE':
            const scorePayload = message.payload as Score;
            if (scorePayload.id.startsWith(this.currentMatchId + '_')) {
              this.updateScoreSignal(scorePayload);
            }
            break;
          case 'CURRENT_MATCH_UPDATE':
            // ... handle current match change ...
            break;
          // Add case for 'LIVE_SCORE_UPDATE' if you want this display to show live increments too
          case 'LIVE_SCORE_UPDATE': // Listen for live updates
            const liveScorePayload = message.payload as Score;
            // Check if it's for the current match we are displaying
            if (liveScorePayload.id.startsWith(this.currentMatchId + '_')) {
              this.updateScoreSignal(liveScorePayload);
            }
            break;
          default:
            console.warn("Received unknown message type:", message.type);
        }
        this.cdr.detectChanges();
      }
    );
  }

  private updateScoreSignal(score: Score) {
    if (score && score.rawScoreData && score.rawScoreData.trim().length > 2) {
      try {
        const data = JSON.parse(score.rawScoreData);
        Object.assign(score, data);
      } catch(e) { console.error("Error parsing score raw data", e); }
    }
    if (score.id.endsWith('_R')) {
      this.redScore.set(score);
    } else if (score.id.endsWith('_B')) {
      this.blueScore.set(score);
    }
  }

  getStatusText(status: number): string {
    return status === 1 ? 'FINAL' : 'IN PROGRESS';
  }
}

