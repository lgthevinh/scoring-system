import { Component, OnInit, ChangeDetectorRef, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatchService } from '../../../core/services/match.service';
import { Score } from '../../../core/models/score.model';

@Component({
  selector: 'app-score-match',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './score-match.component.html',
  styleUrl: './score-match.component.css'
})
export class ScoreMatchComponent implements OnInit {
  matchId: string | null = null;
  redScoreForm: FormGroup;
  blueScoreForm: FormGroup;

  // Signals to hold the full score objects for display
  redScore: WritableSignal<Score | null> = signal(null);
  blueScore: WritableSignal<Score | null> = signal(null);

  isLoading: WritableSignal<boolean> = signal(false);
  errorMessage: WritableSignal<string> = signal('');

  scoreFields = [
    { name: 'robotParked', label: 'Robots Parked' },
    { name: 'robotHanged', label: 'Robots Hanged' },
    { name: 'ballEntered', label: 'Balls Entered' },
    { name: 'minorFault', label: 'Minor Faults (Opponent)' },
    { name: 'majorFault', label: 'Major Faults (Opponent)' },
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private matchService: MatchService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.redScoreForm = this.createScoreForm();
    this.blueScoreForm = this.createScoreForm();
  }

  ngOnInit() {
    this.matchId = this.route.snapshot.paramMap.get('matchId');
    if (this.matchId) {
      this.loadScore('R');
      this.loadScore('B');
    }
  }

  createScoreForm(): FormGroup {
    const group: { [key: string]: any } = {};
    this.scoreFields.forEach(field => {
      group[field.name] = [0];
    });
    return this.fb.group(group);
  }

  loadScore(alliance: 'R' | 'B') {
    if (!this.matchId) return;
    this.isLoading.set(true);
    this.errorMessage.set('');
    const allianceId = `${this.matchId}_${alliance}`;
    const form = alliance === 'R' ? this.redScoreForm : this.blueScoreForm;
    const scoreSignal = alliance === 'R' ? this.redScore : this.blueScore;

    this.matchService.getScore(allianceId).subscribe({
      next: (score: Score) => {
        scoreSignal.set(score); // Update the score signal
        if (score && score.rawScoreData && score.rawScoreData.trim().length > 2) {
          try {
            const data = JSON.parse(score.rawScoreData);
            form.patchValue(data);
            this.cdr.detectChanges();
          } catch (e) {
            console.error('Failed to parse rawScoreData JSON:', e);
            this.errorMessage.set('Error parsing score data.');
          }
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error(`Failed to load score for alliance ${allianceId}:`, err);
        this.errorMessage.set(`Could not load score for ${alliance === 'R' ? 'Red' : 'Blue'} Alliance.`);
        this.isLoading.set(false);
      }
    });
  }

  submitScore(alliance: 'R' | 'B') {
    if (!this.matchId) return;

    const allianceId = `${this.matchId}_${alliance}`;
    const form = alliance === 'R' ? this.redScoreForm : this.blueScoreForm;

    if (form.valid) {
      this.matchService.submitScore(allianceId, form.value).subscribe({
        next: () => {
          alert(`${alliance === 'R' ? 'Red' : 'Blue'} score submitted!`);
          this.loadScore(alliance); // Refresh the score display
        },
        error: (err) => alert(`Error submitting score: ${err.error.error || err.message}`)
      });
    }
  }

  getStatusText(status: number): string {
    return status === 1 ? 'Scored' : 'Not Scored';
  }

  backToMatches() {
    this.router.navigate(['/matches']);
  }
}

