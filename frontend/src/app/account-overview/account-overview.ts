import { Component, ElementRef, OnDestroy, afterNextRender, effect, inject, input, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { BalanceChart } from './balance-chart';
import { OverviewActions } from './overview.actions';
import { overviewFeature } from './overview.feature';

@Component({
  selector: 'app-account-overview',
  imports: [RouterLink, BalanceChart],
  template: `
    <main>
      <a routerLink="/">← All accounts</a>

      @if (account(); as acc) {
        <h1>Account {{ acc.accountId }}</h1>
        <p>Balance: {{ acc.balance }} {{ acc.currency }}</p>
      }

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

  constructor() {
    effect(() => {
      const accountId = this.accountId();
      this.store.dispatch(OverviewActions.reset());
      this.store.dispatch(OverviewActions.loadAccount({ accountId }));
      this.store.dispatch(OverviewActions.loadHistory({ accountId }));
      this.store.dispatch(OverviewActions.loadBalanceSeries({ accountId }));
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
}
