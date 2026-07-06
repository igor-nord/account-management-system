import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { accountsFeature } from '../accounts/accounts.feature';
import { CustomerActions } from '../customer/customer.actions';
import { customerFeature } from '../customer/customer.feature';

@Component({
  selector: 'app-home',
  imports: [FormsModule, RouterLink],
  template: `
    <main class="home">
      <h1>Find a customer</h1>

      <form (ngSubmit)="lookup()">
        <input name="username" [(ngModel)]="username" placeholder="username" autocomplete="off" />
        <button type="submit">Search</button>
      </form>

      @if (loading()) {
        <p>Searching…</p>
      } @else if (error()) {
        <p class="error">{{ error() }}</p>
      } @else if (customer(); as c) {
        <section class="result">
          <h2>{{ c.username }}</h2>
          @if (accounts().length === 0) {
            <p>No accounts.</p>
          } @else {
            <ul>
              @for (account of accounts(); track account.accountCode) {
                <li>
                  <a [routerLink]="['/accounts', account.accountCode]">
                    {{ account.currency }} — {{ account.balance }}
                  </a>
                </li>
              }
            </ul>
          }
        </section>
      }
    </main>
  `,
})
export class Home {
  private readonly store = inject(Store);

  username = '';

  readonly customer = this.store.selectSignal(customerFeature.selectCustomer);
  readonly loading = this.store.selectSignal(customerFeature.selectLoading);
  readonly error = this.store.selectSignal(customerFeature.selectError);
  readonly accounts = this.store.selectSignal(accountsFeature.selectAccounts);

  lookup(): void {
    const username = this.username.trim();
    if (username) {
      this.store.dispatch(CustomerActions.lookup({ username }));
    }
  }
}
