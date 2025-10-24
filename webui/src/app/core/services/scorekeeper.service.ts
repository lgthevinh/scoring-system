import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';

export class ScorekeeperService {
  private apiUrl = '/api/scorekeeper';

  constructor(private http: HttpClient) { }

  setNextMatch(matchId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/set-next-match/${ matchId }`, {});
  }

  startCurrentMatch(): Observable<any> {
    return this.http.post(`${this.apiUrl}/start-current-match`, {});
  }

  commitFinalScore(): Observable<any> {
    return this.http.post(`${this.apiUrl}/commit-final-score`, {});
  }

  overrideScore(allianceId: string, scoreData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/override-score/${allianceId}`, scoreData);
  }

}
