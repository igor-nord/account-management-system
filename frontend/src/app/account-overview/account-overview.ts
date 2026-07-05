import { Component, ElementRef, OnDestroy, afterNextRender, computed, effect, inject, input, viewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { AccountsActions } from '../accounts/accounts.actions';
import { accountsFeature } from '../accounts/accounts.feature';
import { BalanceChart } from './balance-chart';
import { OverviewActions } from './overview.actions';
import { overviewFeature } from './overview.feature';

@Component({
  selector: 'app-account-overview',
  imports: [RouterLink, FormsModule, BalanceChart],
  template: `
    <main>
      <a routerLink="/">← All accounts</a>

      @if (account(); as acc) {
        <h1>Account {{ acc.accountId }}</h1>
        <p>Balance: {{ acc.balance }} {{ acc.currency }}</p>
      }

      <section class="actions">
        <h2>Actions</h2>
        @if (actionError()) {
          <p class="error">{{ actionError() }}</p>
        }
        <form (ngSubmit)="credit()">
          <strong>Credit</strong>
          <input name="creditAmount" [(ngModel)]="creditAmount" placeholder="amount" />
          <input name="creditDescription" [(ngModel)]="creditDescription" placeholder="description" />
          <button type="submit">Add funds</button>
        </form>
        @if (account()?.currency === 'EUR') {
          <form (ngSubmit)="debit()">
            <strong>Debit</strong>
            <input name="debitAmount" [(ngModel)]="debitAmount" placeholder="amount" />
            <input name="debitDescription" [(ngModel)]="debitDescription" placeholder="description" />
            <button type="submit">Withdraw</button>
          </form>
        }
        <form (ngSubmit)="exchange()">
          <strong>Exchange to</strong>
          <select name="exchangeTarget" [(ngModel)]="exchangeTargetId">
            <option [ngValue]="null" disabled>select account</option>
            @for (a of otherAccounts(); track a.accountId) {
              <option [ngValue]="a.accountId">{{ a.accountId }} ({{ a.currency }})</option>
            }
          </select>
          <input name="exchangeAmount" [(ngModel)]="exchangeAmount" placeholder="amount (source currency)" />
          <button type="submit">Exchange</button>
        </form>
      </section>

      <h2>Balance history</h2>
      <app-balance-chart [series]="series()" />

      <h2>Transactions</h2>
      @if (error()) {
        <p class="error">{{ error() }}</p>
      }
      @if (items().length === 0 && !historyLoading()) {
        <p>No transactions.</p>
      }
      <ul>
        @for (t of items(); track t.transactionId) {
          <li>
            <a [routerLink]="['/transactions', t.transactionId]">
              {{ t.createdAt }} · {{ t.type }} · {{ t.amount }} {{ t.currency }} · {{ t.description }}
            </a>
          </li>
        }
      </ul>
      @if (historyLoading()) {
        <p>Loading…</p>
      }
      <div #sentinel></div>
    </main>
  `,
})
export class AccountOverview implements OnDestroy {
  readonly accountId = input.required<number, string>({ transform: (value) => Number(value) });

  private readonly store = inject(Store);
  private readonly sentinel = viewChild.required<ElementRef<HTMLElement>>('sentinel');
  private observer?: IntersectionObserver;

  readonly account = this.store.selectSignal(overviewFeature.selectAccount);
  readonly items = this.store.selectSignal(overviewFeature.selectItems);
  readonly nextCursor = this.store.selectSignal(overviewFeature.selectNextCursor);
  readonly historyLoading = this.store.selectSignal(overviewFeature.selectHistoryLoading);
  readonly series = this.store.selectSignal(overviewFeature.selectSeries);
  readonly error = this.store.selectSignal(overviewFeature.selectError);
  readonly actionError = this.store.selectSignal(overviewFeature.selectActionError);
  private readonly accounts = this.store.selectSignal(accountsFeature.selectAccounts);
  readonly otherAccounts = computed(() =>
    this.accounts().filter((a) => a.accountId !== this.accountId()),
  );

  creditAmount = '';
  creditDescription = '';
  debitAmount = '';
  debitDescription = '';
  exchangeTargetId: number | null = null;
  exchangeAmount = '';

  constructor() {
    effect(() => {
      const accountId = this.accountId();
      this.store.dispatch(OverviewActions.reset());
      this.store.dispatch(OverviewActions.loadAccount({ accountId }));
      this.store.dispatch(OverviewActions.loadHistory({ accountId }));
      this.store.dispatch(OverviewActions.loadBalanceSeries({ accountId }));
      this.store.dispatch(AccountsActions.loadAccounts());
    });

    afterNextRender(() => {
      this.observer = new IntersectionObserver((entries) => {
        if (!entries[0].isIntersecting) {
          return;
        }
        const cursor = this.nextCursor();
        if (cursor && !this.historyLoading()) {
          this.store.dispatch(OverviewActions.loadMoreHistory({ accountId: this.accountId(), cursor }));
        }
      });
      this.observer.observe(this.sentinel().nativeElement);
    });
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
  }

  credit(): void {
    if (!this.creditAmount) {
      return;
    }
    this.store.dispatch(
      OverviewActions.credit({
        accountId: this.accountId(),
        amount: this.creditAmount,
        description: this.creditDescription,
      }),
    );
    this.creditAmount = '';
    this.creditDescription = '';
  }

  debit(): void {
    if (!this.debitAmount) {
      return;
    }
    this.store.dispatch(
      OverviewActions.debit({
        accountId: this.accountId(),
        amount: this.debitAmount,
        description: this.debitDescription,
      }),
    );
    this.debitAmount = '';
    this.debitDescription = '';
  }

  exchange(): void {
    if (!this.exchangeAmount || this.exchangeTargetId === null) {
      return;
    }
    this.store.dispatch(
      OverviewActions.exchange({
        sourceAccountId: this.accountId(),
        targetAccountId: this.exchangeTargetId,
        amount: this.exchangeAmount,
      }),
    );
    this.exchangeAmount = '';
    this.exchangeTargetId = null;
  }
}
