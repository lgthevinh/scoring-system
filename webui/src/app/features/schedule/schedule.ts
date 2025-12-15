import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { MatchDetailDto } from '../../core/models/match.model';
import { FormsModule } from '@angular/forms';
import { TimeUtils } from '../../core/utils/TimeUtils';
import { MatchService, MockMatchService, ProdMatchService } from '../../core/services/match.service';
import { environment } from '../../../environments/environment';

import { ActivatedRoute } from '@angular/router';

@Component({
  providers: [
    {
      provide: MatchService,
      useClass: environment.useFakeData ? MockMatchService : ProdMatchService
    }
  ],
  selector: 'app-schedule',
  imports: [
    FormsModule
  ],
  templateUrl: './schedule.html',
  styleUrl: './schedule.css'
})
export class Schedule implements OnInit {
  viewMatchType: number = 1;

  eventName = 'Qualification Schedule';
  teamsCount: WritableSignal<number> = signal(0);
  matchesPerTeam: WritableSignal<number> = signal(0);
  matchesCount: WritableSignal<number> = signal(0);
  schedule: WritableSignal<MatchDetailDto[]> = signal([]);

  constructor(private matchService: MatchService, private route: ActivatedRoute) {
    console.log(environment.useFakeData ? 'Using MockMatchService' : 'Using ProdMatchService');
  }

  condensed = false;

  printTable() {
    window.print();
  }

  protected readonly TimeUtils = TimeUtils;

  ngOnInit() {
    this.route.data.subscribe(data => {
      if (data['matchType']) {
        this.viewMatchType = data['matchType'];
      }
      if (data['title']) {
        this.eventName = data['title'];
      }
      // Query match schedule from match service
      this.loadSchedule(this.viewMatchType);
    });
  }

  loadSchedule(matchType?: number) {
    const typeToLoad = matchType !== undefined ? matchType : this.viewMatchType;
    this.matchService.getMatches(typeToLoad, false).subscribe(
      {
        next: (matches: MatchDetailDto[]) => {
          this.schedule.set(matches);
          const teamSet = new Set<string>();
          matches.forEach(match => {
            match.redTeams.forEach(team => teamSet.add(team.teamId));
            match.blueTeams.forEach(team => teamSet.add(team.teamId));
          });

          this.teamsCount.set(teamSet.size);
          this.matchesCount.set(matches.length);
          this.matchesPerTeam.set(Math.round(this.matchesCount() * 4 / this.teamsCount()));
        }
      }
    );
    // console.log(`Loaded schedule for match type ${typeToLoad}`);
    // console.log(this.schedule());
  }
}
