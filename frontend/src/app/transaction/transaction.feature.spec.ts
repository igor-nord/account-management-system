import { describe, it, expect } from 'vitest';
import { transactionFeature, TransactionState } from './transaction.feature';
import { TransactionActions } from './transaction.actions';
import { TransactionResponse } from './transaction.models';

const initial: TransactionState = { transaction: null, loading: false, error: null };

const tx: TransactionResponse = {
  transactionId: 'TXN1',
  createdAt: '2026-01-01T00:00:00Z',
  legs: [
    { accountId: 1000011, counterpartyAccountId: 1000006, type: 'CREDIT', amount: '25.00', currency: 'EUR', description: 'Deposit' },
  ],
};

describe('transactionFeature reducer', () => {
  it('stores the transaction and clears loading on success', () => {
    const state = transactionFeature.reducer(
      { ...initial, loading: true },
      TransactionActions.loadTransactionSuccess({ transaction: tx }),
    );
    expect(state.transaction).toEqual(tx);
    expect(state.loading).toBe(false);
  });

  it('stores the error on failure', () => {
    const state = transactionFeature.reducer(
      { ...initial, loading: true },
      TransactionActions.loadTransactionFailure({ error: 'boom' }),
    );
    expect(state.error).toBe('boom');
    expect(state.loading).toBe(false);
  });
});
