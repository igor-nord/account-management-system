import { Routes } from '@angular/router';
import { Home } from './home/home';
import { AccountOverview } from './account-overview/account-overview';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'accounts/:accountId', component: AccountOverview },
];
