import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { MatchDetailDto } from '../models/match.model';
import { Score } from '../models/score.model';
import { RandomUtils } from '../utils/RandomUtils';
import { environment } from '../../../environments/environment';
import { TimeBlock } from '../models/timeblock.model';

export abstract class MatchService {
  protected abstract apiUrl: string;
  protected abstract scoreApiUrl: string;

  abstract getMatches(matchType: number): Observable<MatchDetailDto[]>;
  abstract generateSchedule(scheduleConfig: any): Observable<any>;

  // Add V2 signature
  abstract generateScheduleV2(scheduleConfig: {
    rounds: number;
    startTime: string;        // "yyyy-MM-dd'T'HH:mm"
    matchDuration: number;    // minutes
    timeBlocks: TimeBlock[];  // breaks
  }): Observable<{ message: string }>;

  abstract getScore(allianceId: string): Observable<Score>;
  abstract submitScore(allianceId: string, scoreData: any): Observable<Score>;
}

@Injectable({
  providedIn: 'root'
})
export class ProdMatchService extends MatchService {
  protected apiUrl = environment.apiBaseUrl + '/api/match';
  protected scoreApiUrl = environment.apiBaseUrl + '/api/score';

  constructor(private http: HttpClient) {
    super();
  }

  override getMatches(matchType: number): Observable<MatchDetailDto[]> {
    return this.http.get<MatchDetailDto[]>(`${this.apiUrl}/list/details/${matchType}`);
  }

  override generateSchedule(scheduleConfig: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/schedule/generate`, scheduleConfig);
  }

  // Robust to empty 200 body; always returns an object with message
  override generateScheduleV2(payload: {
    rounds: number;
    startTime: string;
    matchDuration: number;
    timeBlocks: TimeBlock[];
  }): Observable<{ message: string }> {
    return this.http.post(`${this.apiUrl}/schedule/generate/v2`, payload, { observe: 'response' }).pipe(
      map(res => {
        const body = res.body as any;
        const message = body?.message ?? 'Schedule generated successfully.';
        return { message };
      })
    );
  }

  override getScore(allianceId: string): Observable<Score> {
    return this.http.get<Score>(`${this.scoreApiUrl}/alliance/${allianceId}`);
  }

  override submitScore(allianceId: string, scoreData: any): Observable<Score> {
    return this.http.post<Score>(`${this.scoreApiUrl}/submit/${allianceId}`, scoreData);
  }
}

@Injectable({
  providedIn: 'root'
})
export class MockMatchService extends MatchService {
  protected apiUrl = environment.apiBaseUrl + '/api/match';
  protected scoreApiUrl = environment.apiBaseUrl + '/api/score';

  constructor() { super(); }

  override getMatches(matchType: number): Observable<MatchDetailDto[]> {
    return new Observable<MatchDetailDto[]>(observer => {
      const mockMatches: MatchDetailDto[] = [];
      for (let i = 1; i <= 20; i++) {
        mockMatches.push({
          match: {
            id: `match-${i}`,
            matchCode: `Q${i}`,
            matchType: 1,
            matchNumber: i,
            matchField: 1,
            matchStartTime: new Date().toISOString(),
            matchEndTime: null
          },
          redTeams: [
            { teamId: `R${i}1`, teamName: `Red Team ${i}1`, teamSchool: `School ${i}1`, teamRegion: `Region ${i}1` },
            { teamId: `R${i}2`, teamName: `Red Team ${i}2`, teamSchool: `School ${i}2`, teamRegion: `Region ${i}2` },
          ],
          blueTeams: [
            { teamId: `B${i}1`, teamName: `Blue Team ${i}1`, teamSchool: `School ${i}1`, teamRegion: `Region ${i}1` },
            { teamId: `B${i}2`, teamName: `Blue Team ${i}2`, teamSchool: `School ${i}2`, teamRegion: `Region ${i}2` },
          ]
        });
      }
      observer.next(mockMatches);
      observer.complete();
    });
  }

  override generateSchedule(scheduleConfig: any): Observable<any> {
    return new Observable<any>(observer => {
      observer.next({ message: 'Mock schedule generated' });
      observer.complete();
    });
  }

  override generateScheduleV2(_: {
    rounds: number;
    startTime: string;
    matchDuration: number;
    timeBlocks: TimeBlock[];
  }): Observable<{ message: string }> {
    return new Observable(observer => {
      observer.next({ message: 'Mock schedule generated (V2)' });
      observer.complete();
    });
  }

  override getScore(allianceId: string): Observable<Score> {
    return new Observable<Score>(observer => {
      const mockScore: Score = {
        id: allianceId,
        status: 1,
        penaltiesScore: RandomUtils.generateRandomNumber(0, 50),
        totalScore: RandomUtils.generateRandomNumber(100, 300),
        rawScoreData: ""
      };
      observer.next(mockScore);
      observer.complete();
    });
  }

  override submitScore(allianceId: string, scoreData: any): Observable<Score> {
    return new Observable<Score>(observer => {
      const submittedScore: Score = {
        id: allianceId,
        status: 2,
        penaltiesScore: scoreData.penaltiesScore || 0,
        totalScore: scoreData.totalScore || 0,
        rawScoreData: JSON.stringify(scoreData)
      };
      observer.next(submittedScore);
      observer.complete();
    });
  }
}
