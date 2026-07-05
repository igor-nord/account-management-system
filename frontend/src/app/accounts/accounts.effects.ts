import { inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';
import { AccountApi } from './account.api';
import { AccountsActions } from './accounts.actions';

export const loadAccounts$ = createEffect(
  (actions$ = inject(Actions), api = inject(AccountApi)) =>
    actions$.pipe(
      ofType(AccountsActions.loadAccounts),
      switchMap(() =>
        api.getAccounts().pipe(
          map((accounts) => AccountsActions.loadAccountsSuccess({ accounts })),
          catchError((error: Error) => of(AccountsActions.loadAccountsFailure({ error: error.message }))),
        ),
      ),
    ),
  { functional: true },
);
