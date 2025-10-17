import { Routes } from '@angular/router';
import { AuthComponent } from './features/auth/auth.component';
import { TeamListComponent } from './features/scorekeeper/team/team-list/team-list.component';
import { MatchListComponent } from './features/scorekeeper/matches/match-list/match-list.component';
import { ScoreMatchComponent } from './features/scorekeeper/matches/score-match/score-match.component';

export const routes: Routes = [
  // 1. Authentication Route
  { path: 'auth', component: AuthComponent },

  // 2. Feature Routes
  { path: 'teams', component: TeamListComponent },
  { path: 'matches', component: MatchListComponent },
  { path: 'score/:matchId', component: ScoreMatchComponent },

  // 3. Redirect Routes
  { path: '', redirectTo: '/matches', pathMatch: 'full' },
  { path: '**', redirectTo: '/matches' }
];
