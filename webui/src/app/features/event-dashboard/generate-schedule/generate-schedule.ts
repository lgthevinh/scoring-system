import { Component, OnInit, WritableSignal, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatchService, MockMatchService, ProdMatchService } from '../../../core/services/match.service';
import { TeamService } from '../../../core/services/team.service';
import { Team } from '../../../core/models/team.model';
import { environment } from '../../../../environments/environment';
import { TimeBlock } from '../../../core/models/timeblock.model';
import { finalize } from 'rxjs/operators';

type BreakBlockVM = {
  name: string;
  startTimeLocal: string; // yyyy-MM-ddTHH:mm (local)
  durationMinutes: number;
};

@Component({
  selector: 'app-generate-schedule',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [
    { provide: MatchService, useClass: environment.useFakeData ? MockMatchService : ProdMatchService }
  ],
  templateUrl: './generate-schedule.html',
  styleUrls: ['./generate-schedule.css']
})
export class GenerateSchedule implements OnInit {
  // Top summary
  teams: WritableSignal<Team[]> = signal([]);
  teamsCount: WritableSignal<number> = signal(0);
  matchesRequired: WritableSignal<number> = signal(0);
  matchesPerTeamInput = 0;

  // Form fields
  rounds = 3;
  cycleTimeMinutes = 7;
  firstMatchStartLocal = this.toLocalInputValue(new Date());
  breakBlocks: BreakBlockVM[] = [];

  // UI feedback as signals
  running: WritableSignal<boolean> = signal(false);
  successMsg: WritableSignal<string> = signal('');
  errorMsg: WritableSignal<string> = signal('');

  constructor(
    private matchService: MatchService,
    private teamService: TeamService
  ) {}

  ngOnInit(): void {
    this.loadTeams();
  }

  loadTeams() {
    this.teamService.listTeams().subscribe({
      next: (teams) => {
        this.teams.set(teams);
        this.teamsCount.set(teams.length);
        this.updateMatchesRequired();
      },
      error: (err) => console.error('Failed to load teams', err)
    });
  }

  updateMatchesRequired() {
    const count = this.teamsCount();
    this.matchesRequired.set(Math.ceil((this.rounds * count) / 2));
  }

  addBreakBlock() {
    const start = new Date(this.firstMatchStartLocal);
    start.setMinutes(start.getMinutes() + 120);
    this.breakBlocks.push({
      name: 'Break',
      startTimeLocal: this.toLocalInputValue(start),
      durationMinutes: 30
    });
  }

  removeBreakBlock(index: number) {
    this.breakBlocks.splice(index, 1);
  }

  runMatchMaker() {
    this.successMsg.set('');
    this.errorMsg.set('');
    this.running.set(true);

    const payload = {
      rounds: this.rounds,
      startTime: this.toBackendIsoMinute(this.firstMatchStartLocal),
      matchDuration: this.cycleTimeMinutes,
      timeBlocks: this.breakBlocks.map<TimeBlock>(b => ({
        name: (b.name ?? '').trim() || 'Break',
        startTime: this.toBackendIsoMinute(b.startTimeLocal),
        duration: String(b.durationMinutes)
      }))
    };

    this.matchService.generateScheduleV2(payload)
      .pipe(finalize(() => this.running.set(false)))
      .subscribe({
        next: (res) => {
          console.log('generateScheduleV2 success:', res);
          this.successMsg.set(res?.message ?? 'Schedule generated successfully.');
          this.errorMsg.set('');
        },
        error: (err) => {
          console.error('generateScheduleV2 error:', err);
          const msg = err?.error?.message || err?.error?.error || err?.message || 'Failed to generate schedule.';
          this.errorMsg.set(msg);
          this.successMsg.set('');
        }
      });
  }

  // Helpers
  private toLocalInputValue(d: Date): string {
    const pad = (n: number) => n.toString().padStart(2, '0');
    const yyyy = d.getFullYear();
    const MM = pad(d.getMonth() + 1);
    const dd = pad(d.getDate());
    const HH = pad(d.getHours());
    const mm = pad(d.getMinutes());
    return `${yyyy}-${MM}-${dd}T${HH}:${mm}`;
  }

  private toBackendIsoMinute(localInput: string): string {
    return localInput.slice(0, 16);
  }
}
