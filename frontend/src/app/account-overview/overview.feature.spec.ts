import { describe, it, expect } from 'vitest';
import { overviewFeature, OverviewState } from './overview.feature';
import { OverviewActions } from './overview.actions';
import { TransactionSummary } from './overview.models';

const initial: OverviewState = {
  account: null,
  items: [],
  nextCursor: null,
  historyLoading: false,
  series: [],
  error: null,
  actionError: null,
};

const item = (id: string): TransactionSummary => ({
  transactionId: id,
  type: 'CREDIT',
  amount: '1.00',
  currency: 'EUR',
  description: 'x',
  createdAt: '2026-01-01T00:00:00Z',
});

describe('overviewFeature reducer', () => {
  it('sets items and cursor on loadHistorySuccess', () => {
    const state = overviewFeature.reducer(
      initial,
      OverviewActions.loadHistorySuccess({ items: [item('a')], nextCursor: 'c1' }),
    );
    expect(state.items.length).toBe(1);
    expect(state.nextCursor).toBe('c1');
    expect(state.historyLoading).toBe(false);
  });

  it('appends on loadMoreHistorySuccess', () => {
    const seeded: OverviewState = { ...initial, items: [item('a')], nextCursor: 'c1' };
    const state = overviewFeature.reducer(
      seeded,
      OverviewActions.loadMoreHistorySuccess({ items: [item('b')], nextCursor: null }),
    );
    expect(state.items.map((i) => i.transactionId)).toEqual(['a', 'b']);
    expect(state.nextCursor).toBeNull();
  });

  it('clears items on reset', () => {
    const seeded: OverviewState = { ...initial, items: [item('a')] };
    const state = overviewFeature.reducer(seeded, OverviewActions.reset());
    expect(state.items).toEqual([]);
  });

  it('records an action error on actionFailed and clears it on the next action', () => {
    const failed = overviewFeature.reducer(
      initial,
      OverviewActions.actionFailed({ error: 'Insufficient funds' }),
    );
    expect(failed.actionError).toBe('Insufficient funds');

    const retried = overviewFeature.reducer(
      failed,
      OverviewActions.credit({ accountCode: 1000011, amount: '5.00', description: '' }),
    );
    expect(retried.actionError).toBeNull();
  });
});
