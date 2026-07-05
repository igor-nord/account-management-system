import { inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';
import { AccountApi } from '../accounts/account.api';
import { OverviewApi } from './overview.api';
import { OverviewActions } from './overview.actions';

const PAGE_SIZE = 20;

export const loadAccount$ = createEffect(
  (actions$ = inject(Actions), api = inject(AccountApi)) =>
    actions$.pipe(
      ofType(OverviewActions.loadAccount),
      switchMap(({ accountId }) =>
        api.getAccount(accountId).pipe(
          map((account) => OverviewActions.loadAccountSuccess({ account })),
          catchError((error: Error) => of(OverviewActions.loadAccountFailure({ error: error.message }))),
        ),
      ),
    ),
  { functional: true },
);

export const loadHistory$ = createEffect(
  (actions$ = inject(Actions), api = inject(OverviewApi)) =>
    actions$.pipe(
      ofType(OverviewActions.loadHistory),
      switchMap(({ accountId }) =>
        api.getHistory(accountId, PAGE_SIZE).pipe(
          map(({ items, nextCursor }) => OverviewActions.loadHistorySuccess({ items, nextCursor })),
          catchError((error: Error) => of(OverviewActions.loadHistoryFailure({ error: error.message }))),
        ),
      ),
    ),
  { functional: true },
);

export const loadMoreHistory$ = createEffect(
  (actions$ = inject(Actions), api = inject(OverviewApi)) =>
    actions$.pipe(
      ofType(OverviewActions.loadMoreHistory),
      switchMap(({ accountId, cursor }) =>
        api.getHistory(accountId, PAGE_SIZE, cursor).pipe(
          map(({ items, nextCursor }) => OverviewActions.loadMoreHistorySuccess({ items, nextCursor })),
          catchError((error: Error) => of(OverviewActions.loadMoreHistoryFailure({ error: error.message }))),
        ),
      ),
    ),
  { functional: true },
);

export const loadBalanceSeries$ = createEffect(
  (actions$ = inject(Actions), api = inject(OverviewApi)) =>
    actions$.pipe(
      ofType(OverviewActions.loadBalanceSeries),
      switchMap(({ accountId }) =>
        api.getBalanceSeries(accountId).pipe(
          map(({ points }) => OverviewActions.loadBalanceSeriesSuccess({ points })),
          catchError((error: Error) => of(OverviewActions.loadBalanceSeriesFailure({ error: error.message }))),
        ),
      ),
    ),
  { functional: true },
);
