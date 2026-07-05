import { createFeature, createReducer, on } from '@ngrx/store';
import { AccountsActions } from './accounts.actions';
import { AccountSummary } from './account.models';

export interface AccountsState {
  accounts: AccountSummary[];
  loading: boolean;
  error: string | null;
}

const initialState: AccountsState = {
  accounts: [],
  loading: false,
  error: null,
};

export const accountsFeature = createFeature({
  name: 'accounts',
  reducer: createReducer(
    initialState,
    on(AccountsActions.loadAccounts, (state) => ({ ...state, loading: true, error: null, accounts: [] })),
    on(AccountsActions.loadAccountsSuccess, (state, { accounts }) => ({ ...state, loading: false, accounts })),
    on(AccountsActions.loadAccountsFailure, (state, { error }) => ({ ...state, loading: false, error })),
  ),
});

export const { selectAccounts, selectLoading, selectError } = accountsFeature;
