import { describe, it, expect } from 'vitest';
import { accountsFeature } from './accounts.feature';
import { AccountsActions } from './accounts.actions';
import { AccountsState } from './accounts.feature';

const initial: AccountsState = { accounts: [], loading: false, error: null };

describe('accountsFeature reducer', () => {
  it('sets loading on loadAccounts', () => {
    const state = accountsFeature.reducer(initial, AccountsActions.loadAccounts());
    expect(state.loading).toBe(true);
    expect(state.error).toBeNull();
  });

  it('stores accounts and clears loading on success', () => {
    const accounts = [{ accountId: 1000011, currency: 'EUR', balance: '0.00' }];
    const state = accountsFeature.reducer(
      { ...initial, loading: true },
      AccountsActions.loadAccountsSuccess({ accounts }),
    );
    expect(state.loading).toBe(false);
    expect(state.accounts).toEqual(accounts);
  });

  it('stores error on failure', () => {
    const state = accountsFeature.reducer(
      { ...initial, loading: true },
      AccountsActions.loadAccountsFailure({ error: 'boom' }),
    );
    expect(state.loading).toBe(false);
    expect(state.error).toBe('boom');
  });
});
