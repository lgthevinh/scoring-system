import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { Auth } from './features/auth/auth';
import { Schedule } from './features/schedule/schedule';
import {MatchControl} from './features/match-control/match-control';
import {EventDashboard} from './features/event-dashboard/event-dashboard';
import {CreateAccount} from './features/event-dashboard/create-account/create-account';
import {ManageTeam} from './features/event-dashboard/manage-team/manage-team';
import {GenerateSchedule} from './features/event-dashboard/generate-schedule/generate-schedule';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'auth', component: Auth },
  { path: 'schedule', component: Schedule },
  { path: 'event-dashboard', component: EventDashboard },
  { path: 'event-dashboard/create-account', component: CreateAccount },
  { path: 'event-dashboard/manage-team', component: ManageTeam },
  { path: 'event-dashboard/generate-schedule', component: GenerateSchedule },

  { path: 'match-control', component: MatchControl }
];
