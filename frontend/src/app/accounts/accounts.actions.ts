import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { AccountSummary } from './account.models';

export const AccountsActions = createActionGroup({
  source: 'Accounts',
  events: {
    'Load Accounts': emptyProps(),
    'Load Accounts Success': props<{ accounts: AccountSummary[] }>(),
    'Load Accounts Failure': props<{ error: string }>(),
  },
});
