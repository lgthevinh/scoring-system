import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { Auth } from './features/auth/auth';
import { Schedule } from './features/schedule/schedule';
import {MatchControl} from './features/match-control/match-control';
import {EventDashboard} from './features/event-dashboard/event-dashboard';
import {CreateAccount} from './features/event-dashboard/create-account/create-account';
import {ManageTeam} from './features/event-dashboard/manage-team/manage-team';
import {GenerateSchedule} from './features/event-dashboard/generate-schedule/generate-schedule';
import {ScoringDisplay} from './features/scoring-display/scoring-display';
import {BlueAlliance} from './features/referee/match-selection/blue-alliance/blue-alliance';
import {RedAlliance} from './features/referee/match-selection/red-alliance/red-alliance';
import {ScoreTracking} from './features/referee/score-tracking/score-tracking';
import {MatchResults} from './features/match-results/match-results';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'auth', component: Auth },
  { path: 'schedule', component: Schedule },
  { path: 'results', component: MatchResults },
  { path: 'event-dashboard', component: EventDashboard },
  { path: 'event-dashboard/create-account', component: CreateAccount },
  { path: 'event-dashboard/manage-team', component: ManageTeam },
  { path: 'event-dashboard/generate-schedule', component: GenerateSchedule },

  { path: 'display', component: ScoringDisplay },

  { path: 'ref/blue', component: BlueAlliance },
  { path: 'ref/red', component: RedAlliance },

  { path: 'ref/:color/:matchId', component: ScoreTracking },

  { path: 'match-control', component: MatchControl }
];
