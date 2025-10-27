import { Component, Input } from '@angular/core';
import {MatchDetailDto, SampleMatchDetailDto} from '../../core/models/match.model';
import {FormsModule} from '@angular/forms';
import {TimeUtils} from '../../core/utils/TimeUtils';


@Component({
  selector: 'app-schedule',
  imports: [
    FormsModule
  ],
  templateUrl: './schedule.html',
  styleUrl: './schedule.css'
})
export class Schedule {
  @Input() eventName = 'Qualification Schedule';
  @Input() teamsCount = 0;
  @Input() matchesPerTeam = 0;
  @Input() matchesCount = 0;
  @Input() schedule: MatchDetailDto[] = SampleMatchDetailDto(20);

  condensed = false;

  printTable() {
    window.print();
  }

  protected readonly TimeUtils = TimeUtils;
}
