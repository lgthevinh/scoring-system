import {Component, OnInit, WritableSignal, signal, computed} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RankService } from '../../core/services/rank.service';
import { RankingEntry } from '../../core/models/rank.model';

/**
 * Displays FRC team qualification rankings.
 *
 * Features:
 * - Live ranking table with team statistics
 * - Recalculate rankings from all match data
 * - Responsive design for various screen sizes
 * - Loading states and error handling
 */
@Component({
    selector: 'app-rankings',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './rankings.html',
    styleUrls: ['./rankings.css']
})
export class Rankings implements OnInit {

    // Reactive state management
    rankings: WritableSignal<RankingEntry[]> = signal([]);
    isLoadingRankings: WritableSignal<boolean> = signal(false);
    isRecalculating: WritableSignal<boolean> = signal(false);
    lastUpdated: WritableSignal<Date | null> = signal(null);
    errorMessage: WritableSignal<string | null> = signal(null);

    // Computed properties for better UX
    hasRankings = computed(() => this.rankings().length > 0);
    isLoading = computed(() => this.isLoadingRankings() || this.isRecalculating());

    constructor(private rankingService: RankService) { }

    ngOnInit(): void {
        this.loadTeamRankings();
    }

    /**
     * Loads current team ranking data from the server.
     */
    private loadTeamRankings(): void {
        this.isLoadingRankings.set(true);
        this.errorMessage.set(null);

        this.rankingService.getRankStatus().subscribe({
            next: (rankingsData) => {
                this.rankings.set(rankingsData);
                this.lastUpdated.set(new Date());
                this.isLoadingRankings.set(false);

                if (rankingsData.length === 0) {
                    // No rankings yet - this is normal for new events
                    console.info('No rankings available yet - matches may not have been scored');
                }
            },
            error: (error) => {
                console.error('Failed to load team rankings:', error);
                this.errorMessage.set('Unable to load rankings. Please check your connection and try again.');
                this.isLoadingRankings.set(false);
            }
        });
    }

    /**
     * Recalculates rankings from all qualification match data.
     * This is useful when match scores have been updated or corrected.
     */
    recalculateAllRankings(): void {
        if (this.isRecalculating()) {
            return; // Prevent multiple simultaneous recalculations
        }

        const userConfirmed = confirm(
            'This will recalculate rankings from all qualification matches. ' +
            'This may take a moment for events with many matches. Continue?'
        );

        if (!userConfirmed) {
            return;
        }

        this.isRecalculating.set(true);
        this.errorMessage.set(null);

        console.info('Starting ranking recalculation...');

        this.rankingService.recalculateRankings().subscribe({
            next: (success) => {
                if (success) {
                    console.info('Ranking recalculation completed successfully');
                    // Reload rankings to show updated data
                    this.loadTeamRankings();
                } else {
                    this.errorMessage.set('Ranking recalculation failed. Please try again.');
                }
                this.isRecalculating.set(false);
            },
            error: (error) => {
                console.error('Failed to recalculate rankings:', error);
                this.errorMessage.set('Failed to recalculate rankings. Please check the server logs.');
                this.isRecalculating.set(false);
            }
        });
    }

    /**
     * Manually refresh rankings without recalculation.
     */
    refreshRankings(): void {
        if (!this.isLoading()) {
            this.loadTeamRankings();
        }
    }

    /**
     * Clears any error messages.
     */
    clearError(): void {
        this.errorMessage.set(null);
    }
}
