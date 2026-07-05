import { createFeature, createReducer, on } from '@ngrx/store';
import { AccountSummary } from '../accounts/account.models';
import { OverviewActions } from './overview.actions';
import { BalanceSnapshot, TransactionSummary } from './overview.models';

export interface OverviewState {
  account: AccountSummary | null;
  items: TransactionSummary[];
  nextCursor: string | null;
  historyLoading: boolean;
  series: BalanceSnapshot[];
  error: string | null;
}

const initialState: OverviewState = {
  account: null,
  items: [],
  nextCursor: null,
  historyLoading: false,
  series: [],
  error: null,
};

export const overviewFeature = createFeature({
  name: 'accountOverview',
  reducer: createReducer(
    initialState,
    on(OverviewActions.reset, () => initialState),
    on(OverviewActions.loadAccountSuccess, (state, { account }) => ({ ...state, account })),
    on(OverviewActions.loadHistory, (state) => ({
      ...state,
      items: [],
      nextCursor: null,
      historyLoading: true,
      error: null,
    })),
    on(OverviewActions.loadHistorySuccess, (state, { items, nextCursor }) => ({
      ...state,
      items,
      nextCursor,
      historyLoading: false,
    })),
    on(OverviewActions.loadMoreHistory, (state) => ({ ...state, historyLoading: true })),
    on(OverviewActions.loadMoreHistorySuccess, (state, { items, nextCursor }) => ({
      ...state,
      items: [...state.items, ...items],
      nextCursor,
      historyLoading: false,
    })),
    on(OverviewActions.loadBalanceSeriesSuccess, (state, { points }) => ({ ...state, series: points })),
    on(OverviewActions.loadAccountFailure, (state, { error }) => ({ ...state, error })),
    on(OverviewActions.loadHistoryFailure, (state, { error }) => ({ ...state, error, historyLoading: false })),
    on(OverviewActions.loadMoreHistoryFailure, (state, { error }) => ({ ...state, error, historyLoading: false })),
    on(OverviewActions.loadBalanceSeriesFailure, (state, { error }) => ({ ...state, error })),
  ),
});

export const {
  selectAccount,
  selectItems,
  selectNextCursor,
  selectHistoryLoading,
  selectSeries,
  selectError,
} = overviewFeature;
