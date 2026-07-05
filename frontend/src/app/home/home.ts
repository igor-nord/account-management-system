import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { AccountsActions } from '../accounts/accounts.actions';
import { accountsFeature } from '../accounts/accounts.feature';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  template: `
    <main>
      <h1>Your accounts</h1>

      @if (loading()) {
        <p>Loading…</p>
      } @else if (error()) {
        <p class="error">{{ error() }}</p>
      } @else if (accounts().length === 0) {
        <p>No accounts.</p>
      } @else {
        <ul>
          @for (account of accounts(); track account.accountId) {
            <li>
              <a [routerLink]="['/accounts', account.accountId]">
                {{ account.currency }} — {{ account.balance }}
              </a>
            </li>
          }
        </ul>
      }
    </main>
  `,
})
export class Home {
  private readonly store = inject(Store);

  readonly accounts = this.store.selectSignal(accountsFeature.selectAccounts);
  readonly loading = this.store.selectSignal(accountsFeature.selectLoading);
  readonly error = this.store.selectSignal(accountsFeature.selectError);

  constructor() {
    this.store.dispatch(AccountsActions.loadAccounts());
  }
}
