import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class RefereeService {
  private apiUrl = environment.apiBaseUrl + '/api/ref';

  constructor(
    private http: HttpClient
  ) { }

  submitFinalScore(color: string, allianceId: string, scoreData: any) {
    console.log('Submitting final score:', color, allianceId, scoreData);
    return this.http.post(`${this.apiUrl}/submit/${color}/${allianceId}/final-score`, scoreData);
  }
}
