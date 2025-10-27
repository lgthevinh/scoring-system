import { Routes } from '@angular/router';
import { Home } from './features/home/home';
import { Auth } from './features/auth/auth';
import { Schedule } from './features/schedule/schedule';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'auth', component: Auth },
  { path: 'schedule', component: Schedule }
];
