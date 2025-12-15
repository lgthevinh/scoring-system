import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, retry, catchError, timeout } from 'rxjs';
import { RankingEntry } from '../models/rank.model';
import { environment } from '../../../environments/environment';

/**
 * Service for managing FRC team ranking operations.
 *
 * Handles communication with the backend ranking API including:
 * - Fetching current team rankings
 * - Recalculating rankings from match data
 * - Error handling and retry logic
 *
 * @author FRC Scoring System Team
 */
@Injectable({
    providedIn: 'root'
})
export class RankService {
    private readonly apiUrl = environment.apiBaseUrl + '/api/rank';
    private readonly REQUEST_TIMEOUT = 30000; // 30 seconds
    private readonly MAX_RETRIES = 2;

    constructor(private http: HttpClient) { }

    /**
     * Fetches current qualification rankings from the server.
     *
     * @returns Observable stream of ranking entries sorted by rank
     */
    getRankStatus(): Observable<RankingEntry[]> {
        return this.http.get<RankingEntry[]>(`${this.apiUrl}/status`).pipe(
            timeout<RankingEntry[]>(this.REQUEST_TIMEOUT),
            retry<RankingEntry[]>(this.MAX_RETRIES),
            catchError<RankingEntry[], Observable<RankingEntry[]>>(this.handleError('getRankStatus'))
        );
    }

    /**
     * Triggers recalculation of rankings from all qualification match data.
     * This is useful when match scores have been corrected or updated.
     *
     * @returns Observable indicating success/failure of recalculation
     */
    recalculateRankings(): Observable<boolean> {
        return this.http.post<boolean>(`${this.apiUrl}/recalculate`, {}).pipe(
            timeout<boolean>(this.REQUEST_TIMEOUT * 2), // Longer timeout for recalculation
            retry<boolean>(0), // Don't retry recalculations as they can be expensive
            catchError<boolean, Observable<boolean>>(this.handleError('recalculateRankings'))
        );
    }

    /**
     * Centralized error handling for HTTP requests.
     *
     * @param operation Name of the operation that failed
     * @returns Error handling function
     */
    private handleError<T>(operation = 'operation') {
        return (error: HttpErrorResponse): Observable<T> => {
            let errorMessage = `Operation '${operation}' failed`;

            if (error.error instanceof ErrorEvent) {
                // Client-side or network error
                errorMessage += `: ${error.error.message}`;
            } else {
                // Backend returned an error
                errorMessage += `: ${error.status} - ${error.message}`;

                // Add specific error details for common cases
                if (error.status === 0) {
                    errorMessage += ' (Unable to connect to server)';
                } else if (error.status === 500) {
                    errorMessage += ' (Server error - check server logs)';
                } else if (error.status >= 400 && error.status < 500) {
                    errorMessage += ' (Request error - check input data)';
                }
            }

            console.error(`${operation} failed:`, error);
            return throwError(() => new Error(errorMessage));
        };
    }
}
