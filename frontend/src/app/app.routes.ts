import { Routes } from '@angular/router';
import { GameTableComponent } from './game/game-table/game-table.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: 'game',
    component: GameTableComponent,
    canActivate: [authGuard]
  },
  {
    path: '',
    redirectTo: '/game',
    pathMatch: 'full'
  },
  {
    path: '**', // Wildcard route for a 404 page
    redirectTo: '/login'
  }
];
