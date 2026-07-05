import { inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';
import { TransactionApi } from './transaction.api';
import { TransactionActions } from './transaction.actions';

export const loadTransaction$ = createEffect(
  (actions$ = inject(Actions), api = inject(TransactionApi)) =>
    actions$.pipe(
      ofType(TransactionActions.loadTransaction),
      switchMap(({ transactionId }) =>
        api.getTransaction(transactionId).pipe(
          map((transaction) => TransactionActions.loadTransactionSuccess({ transaction })),
          catchError((error: Error) => of(TransactionActions.loadTransactionFailure({ error: error.message }))),
        ),
      ),
    ),
  { functional: true },
);
