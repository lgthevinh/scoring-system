import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TeamService } from '../../../core/services/team.service';
import { Team } from '../../../core/models/team.model';

@Component({
  selector: 'app-team-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './team-list.component.html'
})
export class TeamListComponent implements OnInit {
  // Use WritableSignals to hold the component's state.
  teams: WritableSignal<Team[]> = signal([]);
  isLoading: WritableSignal<boolean> = signal(false);
  errorMessage: WritableSignal<string> = signal('');

  showAddTeamModal = false;
  isEditing = false;
  teamForm: FormGroup;

  constructor(private teamService: TeamService, private fb: FormBuilder) {
    this.teamForm = this.fb.group({
      teamId: ['', Validators.required],
      teamName: ['', Validators.required],
      teamSchool: [''],
      teamRegion: ['']
    });
  }

  ngOnInit() {
    this.loadTeams();
  }

  loadTeams() {
    this.isLoading.set(true);
    this.errorMessage.set('');
    this.teamService.getTeams().subscribe({
      next: (data) => {
        // Update the signal's value using .set()
        this.teams.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load teams', err);
        this.errorMessage.set('Could not load teams. Please try again later.');
        this.isLoading.set(false);
      }
    });
  }

  editTeam(team: Team) {
    this.isEditing = true;
    this.teamForm.patchValue(team);
    this.showAddTeamModal = true;
  }

  deleteTeam(id: string) {
    if (confirm('Are you sure you want to delete this team?')) {
      this.teamService.deleteTeam(id).subscribe(() => this.loadTeams());
    }
  }

  onTeamSubmit() {
    if (this.teamForm.invalid) return;

    const apiCall = this.isEditing
      ? this.teamService.updateTeam(this.teamForm.value)
      : this.teamService.addTeam(this.teamForm.value);

    apiCall.subscribe(() => {
      this.loadTeams();
      this.showAddTeamModal = false;
    });
  }
}

