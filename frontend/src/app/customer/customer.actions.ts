import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { CustomerResponse } from './customer.models';

export const CustomerActions = createActionGroup({
  source: 'Customer',
  events: {
    'Lookup': props<{ username: string }>(),
    'Lookup Success': props<{ customer: CustomerResponse }>(),
    'Lookup Failure': props<{ error: string }>(),
    'Clear': emptyProps(),
  },
});
