import { inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, mergeMap, of, switchMap } from 'rxjs';
import { AccountApi } from '../accounts/account.api';
import { AccountsActions } from '../accounts/accounts.actions';
import { OverviewApi } from './overview.api';
import { OverviewActions } from './overview.actions';

const PAGE_SIZE = 20;

function message(error: unknown): string {
  const detail = (error as { error?: { detail?: string } }).error?.detail;
  const fallback = (error as { message?: string }).message;
  return detail ?? fallback ?? 'Request failed';
}

export const loadAccount$ = createEffect(
  (actions$ = inject(Actions), api = inject(AccountApi)) =>
    actions$.pipe(
      ofType(OverviewActions.loadAccount),
      switchMap(({ accountId }) =>
        api.getAccount(accountId).pipe(
          map((account) => OverviewActions.loadAccountSuccess({ account })),
          catchError((error) => of(OverviewActions.loadAccountFailure({ error: message(error) }))),
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
          catchError((error) => of(OverviewActions.loadHistoryFailure({ error: message(error) }))),
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
          catchError((error) => of(OverviewActions.loadMoreHistoryFailure({ error: message(error) }))),
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
          catchError((error) => of(OverviewActions.loadBalanceSeriesFailure({ error: message(error) }))),
        ),
      ),
    ),
  { functional: true },
);

export const credit$ = createEffect(
  (actions$ = inject(Actions), api = inject(OverviewApi)) =>
    actions$.pipe(
      ofType(OverviewActions.credit),
      mergeMap(({ accountId, amount, description }) =>
        api.credit(accountId, amount, description).pipe(
          map(() => OverviewActions.actionSucceeded({ accountId })),
          catchError((error) => of(OverviewActions.actionFailed({ error: message(error) }))),
        ),
      ),
    ),
  { functional: true },
);

export const debit$ = createEffect(
  (actions$ = inject(Actions), api = inject(OverviewApi)) =>
    actions$.pipe(
      ofType(OverviewActions.debit),
      mergeMap(({ accountId, amount, description }) =>
        api.debit(accountId, amount, description).pipe(
          map(() => OverviewActions.actionSucceeded({ accountId })),
          catchError((error) => of(OverviewActions.actionFailed({ error: message(error) }))),
        ),
      ),
    ),
  { functional: true },
);

export const exchange$ = createEffect(
  (actions$ = inject(Actions), api = inject(OverviewApi)) =>
    actions$.pipe(
      ofType(OverviewActions.exchange),
      mergeMap(({ sourceAccountId, targetAccountId, amount }) =>
        api.exchange(sourceAccountId, targetAccountId, amount).pipe(
          map(() => OverviewActions.actionSucceeded({ accountId: sourceAccountId })),
          catchError((error) => of(OverviewActions.actionFailed({ error: message(error) }))),
        ),
      ),
    ),
  { functional: true },
);

export const reloadAfterAction$ = createEffect(
  (actions$ = inject(Actions)) =>
    actions$.pipe(
      ofType(OverviewActions.actionSucceeded),
      switchMap(({ accountId }) =>
        of(
          OverviewActions.loadAccount({ accountId }),
          OverviewActions.loadHistory({ accountId }),
          OverviewActions.loadBalanceSeries({ accountId }),
          AccountsActions.loadAccounts(),
        ),
      ),
    ),
  { functional: true },
);
