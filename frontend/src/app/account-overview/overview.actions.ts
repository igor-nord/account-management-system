import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { AccountSummary } from '../accounts/account.models';
import { BalanceSnapshot, TransactionSummary } from './overview.models';

export const OverviewActions = createActionGroup({
  source: 'Account Overview',
  events: {
    'Reset': emptyProps(),
    'Load Account': props<{ accountId: number }>(),
    'Load Account Success': props<{ account: AccountSummary }>(),
    'Load Account Failure': props<{ error: string }>(),
    'Load History': props<{ accountId: number }>(),
    'Load History Success': props<{ items: TransactionSummary[]; nextCursor: string | null }>(),
    'Load History Failure': props<{ error: string }>(),
    'Load More History': props<{ accountId: number; cursor: string }>(),
    'Load More History Success': props<{ items: TransactionSummary[]; nextCursor: string | null }>(),
    'Load More History Failure': props<{ error: string }>(),
    'Load Balance Series': props<{ accountId: number }>(),
    'Load Balance Series Success': props<{ points: BalanceSnapshot[] }>(),
    'Load Balance Series Failure': props<{ error: string }>(),
  },
});
