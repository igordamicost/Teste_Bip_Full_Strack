import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
  { path: '', loadComponent: () => import('./pages/beneficio-list/beneficio-list.component').then(m => m.BeneficioListComponent), canActivate: [authGuard] },
  { path: 'novo', loadComponent: () => import('./pages/beneficio-form/beneficio-form.component').then(m => m.BeneficioFormComponent), canActivate: [authGuard] },
  { path: 'editar/:id', loadComponent: () => import('./pages/beneficio-form/beneficio-form.component').then(m => m.BeneficioFormComponent), canActivate: [authGuard] },
  { path: 'transferir', loadComponent: () => import('./pages/transfer/transfer.component').then(m => m.TransferComponent), canActivate: [authGuard] },
  { path: '**', redirectTo: '' },
];
