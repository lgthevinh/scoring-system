import { Routes } from '@angular/router';

import { Auth } from './auth/auth';
import { Dashboard } from './dashboard/dashboard';

export const routes: Routes = [
  { path: 'auth', component: Auth },
  { path: 'dashboard', component: Dashboard }
];
