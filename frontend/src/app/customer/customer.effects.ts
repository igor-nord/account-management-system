import { inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap, tap } from 'rxjs';
import { AccountsActions } from '../accounts/accounts.actions';
import { CurrentCustomer } from '../core/current-customer';
import { CustomerApi } from './customer.api';
import { CustomerActions } from './customer.actions';

export const lookup$ = createEffect(
  (actions$ = inject(Actions), api = inject(CustomerApi)) =>
    actions$.pipe(
      ofType(CustomerActions.lookup),
      switchMap(({ username }) =>
        api.findByUsername(username).pipe(
          map((customer) => CustomerActions.lookupSuccess({ customer })),
          catchError(() => of(CustomerActions.lookupFailure({ error: 'Customer not found.' }))),
        ),
      ),
    ),
  { functional: true },
);

export const loadAccountsAfterLookup$ = createEffect(
  (actions$ = inject(Actions), currentCustomer = inject(CurrentCustomer)) =>
    actions$.pipe(
      ofType(CustomerActions.lookupSuccess),
      tap(({ customer }) => currentCustomer.set(customer.username)),
      map(() => AccountsActions.loadAccounts()),
    ),
  { functional: true },
);
