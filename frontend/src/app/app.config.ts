import { ApplicationConfig, isDevMode, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideStore, provideState } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';

import { routes } from './app.routes';
import { customerIdInterceptor } from './core/customer-id.interceptor';
import { accountsFeature } from './accounts/accounts.feature';
import { loadAccounts$ } from './accounts/accounts.effects';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([customerIdInterceptor])),
    provideStore(),
    provideState(accountsFeature),
    provideEffects({ loadAccounts$ }),
    provideStoreDevtools({ maxAge: 25, logOnly: !isDevMode() }),
  ],
};
