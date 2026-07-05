import { createFeature, createReducer, on } from '@ngrx/store';
import { CustomerActions } from './customer.actions';
import { CustomerResponse } from './customer.models';

export interface CustomerState {
  customer: CustomerResponse | null;
  loading: boolean;
  error: string | null;
}

const initialState: CustomerState = {
  customer: null,
  loading: false,
  error: null,
};

export const customerFeature = createFeature({
  name: 'customer',
  reducer: createReducer(
    initialState,
    on(CustomerActions.lookup, (state) => ({ ...state, loading: true, error: null, customer: null })),
    on(CustomerActions.lookupSuccess, (state, { customer }) => ({ ...state, loading: false, customer })),
    on(CustomerActions.lookupFailure, (state, { error }) => ({ ...state, loading: false, error })),
    on(CustomerActions.clear, () => initialState),
  ),
});

export const { selectCustomer, selectLoading, selectError } = customerFeature;
