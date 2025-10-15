import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Team } from '../models/team.model';

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  private apiUrl = '/api/team';

  constructor(private http: HttpClient) { }

  getTeams(): Observable<Team[]> {
    return this.http.get<Team[]>(`${this.apiUrl}/list`);
  }

  addTeam(team: Team): Observable<Team> {
    return this.http.post<Team>(`${this.apiUrl}/create`, team);
  }

  updateTeam(team: Team): Observable<Team> {
    return this.http.put<Team>(`${this.apiUrl}/update`, team);
  }

  deleteTeam(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`);
  }
}
