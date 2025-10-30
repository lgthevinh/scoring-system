import { Component, OnInit, WritableSignal, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatchService, MockMatchService, ProdMatchService } from '../../../core/services/match.service';
import { TeamService } from '../../../core/services/team.service';
import { Team } from '../../../core/models/team.model';
import { environment } from '../../../../environments/environment';
import { TimeBlock } from '../../../core/models/timeblock.model';

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
  matchesPerTeamInput = 0; // optional input similar to FTC header (not sent to backend)

  // Form fields
  rounds = 3;
  cycleTimeMinutes = 7; // mapped to matchDuration
  firstMatchStartLocal = this.toLocalInputValue(new Date()); // datetime-local control value
  breakBlocks: BreakBlockVM[] = [];

  // UI feedback
  running = false;
  successMsg = '';
  errorMsg = '';

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
      error: (err) => {
        console.error('Failed to load teams', err);
      }
    });
  }

  updateMatchesRequired() {
    // For 2v2: matches needed ~ rounds * teamsCount / 2
    const count = this.teamsCount();
    this.matchesRequired.set(Math.ceil((this.rounds * count) / 2));
  }

  addBreakBlock() {
    // Default 30-min break starting 2 hours after first match
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
    this.successMsg = '';
    this.errorMsg = '';
    this.running = true;

    const payload = {
      rounds: this.rounds,
      startTime: this.toBackendIsoMinute(this.firstMatchStartLocal), // "yyyy-MM-dd'T'HH:mm"
      matchDuration: this.cycleTimeMinutes,
      timeBlocks: this.breakBlocks.map<TimeBlock>(b => ({
        name: b.name?.trim() || 'Break',
        startTime: this.toBackendIsoMinute(b.startTimeLocal),
        duration: String(b.durationMinutes)
      }))
    };

    this.matchService.generateScheduleV2(payload).subscribe({
      next: (res) => {
        this.successMsg = (res?.message) || 'Schedule generated successfully.';
        this.running = false;
      },
      error: (err) => {
        this.errorMsg = err?.error?.error || 'Failed to generate schedule.';
        this.running = false;
      }
    });
  }

  // Helpers

  // Convert Date -> "yyyy-MM-ddTHH:mm" in local time (for datetime-local input)
  private toLocalInputValue(d: Date): string {
    const pad = (n: number) => n.toString().padStart(2, '0');
    const yyyy = d.getFullYear();
    const MM = pad(d.getMonth() + 1);
    const dd = pad(d.getDate());
    const HH = pad(d.getHours());
    const mm = pad(d.getMinutes());
    return `${yyyy}-${MM}-${dd}T${HH}:${mm}`;
  }

  // Convert a local datetime-local string -> backend expected "yyyy-MM-dd'T'HH:mm" (no timezone suffix)
  private toBackendIsoMinute(localInput: string): string {
    // Input is already "yyyy-MM-ddTHH:mm" in local time; pass through.
    // Ensure seconds are not present.
    return localInput.slice(0, 16);
  }
}
