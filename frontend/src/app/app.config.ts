import { ApplicationConfig, isDevMode, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideStore, provideState } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';

import { routes } from './app.routes';
import { customerIdInterceptor } from './core/customer-id.interceptor';
import { accountsFeature } from './accounts/accounts.feature';
import { loadAccounts$ } from './accounts/accounts.effects';
import { overviewFeature } from './account-overview/overview.feature';
import {
  loadAccount$,
  loadBalanceSeries$,
  loadHistory$,
  loadMoreHistory$,
} from './account-overview/overview.effects';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withInterceptors([customerIdInterceptor])),
    provideStore(),
    provideState(accountsFeature),
    provideState(overviewFeature),
    provideEffects({ loadAccounts$ }),
    provideEffects({ loadAccount$, loadHistory$, loadMoreHistory$, loadBalanceSeries$ }),
    provideStoreDevtools({ maxAge: 25, logOnly: !isDevMode() }),
  ],
};
