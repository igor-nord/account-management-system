import { createFeature, createReducer, on } from '@ngrx/store';
import { TransactionActions } from './transaction.actions';
import { TransactionResponse } from './transaction.models';

export interface TransactionState {
  transaction: TransactionResponse | null;
  loading: boolean;
  error: string | null;
}

const initialState: TransactionState = {
  transaction: null,
  loading: false,
  error: null,
};

export const transactionFeature = createFeature({
  name: 'transaction',
  reducer: createReducer(
    initialState,
    on(TransactionActions.loadTransaction, (state) => ({
      ...state,
      loading: true,
      error: null,
      transaction: null,
    })),
    on(TransactionActions.loadTransactionSuccess, (state, { transaction }) => ({
      ...state,
      loading: false,
      transaction,
    })),
    on(TransactionActions.loadTransactionFailure, (state, { error }) => ({
      ...state,
      loading: false,
      error,
    })),
  ),
});

export const { selectTransaction, selectLoading, selectError } = transactionFeature;
