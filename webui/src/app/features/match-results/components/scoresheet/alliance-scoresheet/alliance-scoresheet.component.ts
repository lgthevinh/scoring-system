import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Score } from '../../../../../core/models/score.model';
import { Team } from '../../../../../core/models/team.model';
import { ScoresheetConfig } from '../scoresheet.config';

@Component({
  selector: 'app-alliance-scoresheet',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="alliance-container">
      <div class="alliance-header text-white text-center py-2 mb-2" 
           [ngClass]="{'bg-danger': alliance === 'red', 'bg-primary': alliance === 'blue'}">
        <h3 class="m-0">{{ alliance === 'red' ? 'Red Alliance' : 'Blue Alliance' }}</h3>
      </div>

      <div class="match-info text-center mb-3 fw-bold" *ngIf="matchInfo">
        Match: <span class="text-decoration-underline">{{ matchInfo.matchCode }}</span>
        Field: <span class="text-decoration-underline">{{ matchInfo.fieldNumber }}</span>
      </div>

      <div class="text-center mb-3">
        <div class="d-flex justify-content-center gap-3 flex-wrap">
          <span *ngFor="let team of teams" class="fw-bold fs-5">
            {{ team.teamId }}
          </span>
        </div>
      </div>

      <div class="period-container mb-4" *ngFor="let period of config?.periods">
        <div class="period-header text-center bg-secondary text-white fw-bold py-1 mb-2">
          {{ period.title }}
        </div>

        <div class="row">
          <div class="col-12" *ngFor="let section of period.sections">
            
            <!-- Fields Section -->
            <ng-container *ngIf="section.type === 'fields'">
              <div class="section-title text-center fw-bold text-uppercase mb-2" *ngIf="section.title">{{ section.title }}</div>
              <div class="row mb-3">
                <div class="col" *ngFor="let field of section.fields">
                  <div class="d-flex justify-content-between align-items-center border-bottom pb-1 mb-1">
                    <span class="fw-bold small">{{ field.label }}</span>
                    <ng-container *ngIf="!editable">
                      <span class="fw-bold">{{ getValue(scoreData, field.key) ?? 0 }}</span>
                    </ng-container>
                    <ng-container *ngIf="editable">
                      <input type="number" class="form-control form-control-sm w-25 text-end"
                             [ngModel]="getValue(scoreData, field.key) ?? 0"
                             (ngModelChange)="setValue(field.key, $event)">
                    </ng-container>
                  </div>
                </div>
              </div>
            </ng-container>

            <!-- Team Table Section -->
            <ng-container *ngIf="section.type === 'team-table'">
              <div class="section-title text-center fw-bold text-uppercase mb-2" *ngIf="section.title">{{ section.title }}</div>
              <table class="table table-sm table-borderless text-center align-middle">
                <thead>
                  <tr>
                    <th></th> <!-- Team ID column -->
                    <th *ngFor="let col of section.columns" class="small fw-bold text-uppercase">{{ col.label }}</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let team of teams">
                    <td class="fw-bold">{{ team.teamId }}</td>
                    <td *ngFor="let col of section.columns">
                      <ng-container [ngSwitch]="col.type">
                        <input *ngSwitchCase="'checkbox'" type="checkbox" 
                               [checked]="getTeamValue(scoreData, team.teamId, col.key)" 
                               [disabled]="!editable"
                               (change)="setTeamValue(team.teamId, col.key, $any($event.target).checked)">
                        <ng-container *ngSwitchCase="'text'">
                          <span *ngIf="!editable">{{ getTeamValue(scoreData, team.teamId, col.key) || 'None' }}</span>
                          <input *ngIf="editable" type="text" class="form-control form-control-sm"
                                 [ngModel]="getTeamValue(scoreData, team.teamId, col.key)"
                                 (ngModelChange)="setTeamValue(team.teamId, col.key, $event)">
                        </ng-container>
                        <ng-container *ngSwitchDefault>
                           <span *ngIf="!editable">{{ getTeamValue(scoreData, team.teamId, col.key) }}</span>
                           <input *ngIf="editable" type="number" class="form-control form-control-sm"
                                  [ngModel]="getTeamValue(scoreData, team.teamId, col.key)"
                                  (ngModelChange)="setTeamValue(team.teamId, col.key, $event)">
                        </ng-container>
                      </ng-container>
                    </td>
                  </tr>
                </tbody>
              </table>
            </ng-container>

          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .alliance-header h3 { font-weight: bold; }
    .match-info { font-size: 1.1rem; }
  `]
})
export class AllianceScoresheetComponent implements OnChanges {
  @Input() score: Score | undefined;
  @Input() teams: Team[] = [];
  @Input() config: ScoresheetConfig | undefined;
  @Input() alliance: 'red' | 'blue' = 'red';
  @Input() matchInfo: { matchCode: string, fieldNumber: number } | null = null;
  @Input() editable: boolean = false;
  @Output() scoreChange = new EventEmitter<any>();

  scoreData: any = {};

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['score'] && this.score) {
      this.scoreData = this.parseScore(this.score);
    }
  }

  private parseScore(score: Score | undefined): any {
    if (!score || !score.rawScoreData) return {};
    try {
      return JSON.parse(score.rawScoreData);
    } catch (e) {
      console.error('Failed to parse score data', e);
      return {};
    }
  }

  getValue(data: any, key: string): any {
    if (!data) return null;
    return key.split('.').reduce((acc, part) => acc && acc[part], data);
  }

  getTeamValue(data: any, teamId: string, key: string): any {
    if (data?.teams && data.teams[teamId]) {
      return data.teams[teamId][key];
    }
    return null;
  }

  setValue(key: string, value: any) {
    if (!this.scoreData) this.scoreData = {};

    // Handle nested keys if necessary, though simple assignment works for flat structure
    // For this implementation, we assume flat keys or simple object structure
    // If keys are like 'auto.parked', we need to split
    const parts = key.split('.');
    let current = this.scoreData;
    for (let i = 0; i < parts.length - 1; i++) {
      if (!current[parts[i]]) current[parts[i]] = {};
      current = current[parts[i]];
    }
    current[parts[parts.length - 1]] = value;

    console.log('AllianceScoresheet: Score changed', this.scoreData);
    this.scoreChange.emit(this.scoreData);
  }

  setTeamValue(teamId: string, key: string, value: any) {
    if (!this.scoreData) this.scoreData = {};
    if (!this.scoreData.teams) this.scoreData.teams = {};
    if (!this.scoreData.teams[teamId]) this.scoreData.teams[teamId] = {};

    this.scoreData.teams[teamId][key] = value;
    console.log('AllianceScoresheet: Team score changed', this.scoreData);
    this.scoreChange.emit(this.scoreData);
  }
}
