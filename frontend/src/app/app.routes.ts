import { Routes } from '@angular/router';
import { Home } from './home/home';
import { AccountOverview } from './account-overview/account-overview';
import { TransactionOverview } from './transaction/transaction-overview';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'accounts/:accountId', component: AccountOverview },
  { path: 'transactions/:transactionId', component: TransactionOverview },
];
