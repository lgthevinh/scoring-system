import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';
import {MatchDetailDto} from '../models/match.model';

@Injectable({ providedIn: 'root' })
export class SyncService {
  private apiUrl = environment.apiBaseUrl + '/api/sync';

  constructor(private http: HttpClient) {}

  syncPlayingMatches(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/playing-matches`);
  }
}
