import { createActionGroup, props } from '@ngrx/store';
import { TransactionResponse } from './transaction.models';

export const TransactionActions = createActionGroup({
  source: 'Transaction',
  events: {
    'Load Transaction': props<{ transactionId: string }>(),
    'Load Transaction Success': props<{ transaction: TransactionResponse }>(),
    'Load Transaction Failure': props<{ error: string }>(),
  },
});
