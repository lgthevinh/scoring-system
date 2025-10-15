import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MatchDetailDto } from '../models/match.model';
import { Score } from '../models/score.model';

@Injectable({
  providedIn: 'root'
})
export class MatchService {
  private apiUrl = '/api/match';
  private scoreApiUrl = '/api/score';

  constructor(private http: HttpClient) { }

  getMatches(matchType: number): Observable<MatchDetailDto[]> {
    return this.http.get<MatchDetailDto[]>(`${this.apiUrl}/list/details/${matchType}`);
  }

  generateSchedule(scheduleConfig: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/schedule/generate`, scheduleConfig);
  }

  getScore(allianceId: string): Observable<Score> {
    return this.http.get<Score>(`${this.scoreApiUrl}/alliance/${allianceId}`);
  }

  submitScore(allianceId: string, scoreData: any): Observable<Score> {
    return this.http.post<Score>(`${this.scoreApiUrl}/submit/${allianceId}`, scoreData);
  }
}

