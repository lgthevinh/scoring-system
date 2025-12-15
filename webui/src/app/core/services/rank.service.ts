import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RankingEntry } from '../models/rank.model';
import {environment} from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class RankService {
    private apiUrl = environment.apiBaseUrl + '/api/rank';

    constructor(private http: HttpClient) { }

    getRankStatus(): Observable<RankingEntry[]> {
        return this.http.get<RankingEntry[]>(`${this.apiUrl}/status`);
    }

    recalculateRankings(): Observable<boolean> {
        return this.http.post<boolean>(`${this.apiUrl}/recalculate`, {});
    }
}
