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
      switchMap(({ accountCode }) =>
        api.getAccount(accountCode).pipe(
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
      switchMap(({ accountCode }) =>
        api.getHistory(accountCode, PAGE_SIZE).pipe(
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
      switchMap(({ accountCode, cursor }) =>
        api.getHistory(accountCode, PAGE_SIZE, cursor).pipe(
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
      switchMap(({ accountCode }) =>
        api.getBalanceSeries(accountCode).pipe(
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
      mergeMap(({ accountCode, amount, description }) =>
        api.credit(accountCode, amount, description).pipe(
          map(() => OverviewActions.actionSucceeded({ accountCode })),
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
      mergeMap(({ accountCode, amount, description }) =>
        api.debit(accountCode, amount, description).pipe(
          map(() => OverviewActions.actionSucceeded({ accountCode })),
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
      mergeMap(({ sourceAccountCode, targetAccountCode, amount }) =>
        api.exchange(sourceAccountCode, targetAccountCode, amount).pipe(
          map(() => OverviewActions.actionSucceeded({ accountCode: sourceAccountCode })),
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
      switchMap(({ accountCode }) =>
        of(
          OverviewActions.loadAccount({ accountCode }),
          OverviewActions.loadHistory({ accountCode }),
          OverviewActions.loadBalanceSeries({ accountCode }),
          AccountsActions.loadAccounts(),
        ),
      ),
    ),
  { functional: true },
);
