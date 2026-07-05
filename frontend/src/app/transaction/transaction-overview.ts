import { Component, effect, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { TransactionApi } from './transaction.api';
import { TransactionActions } from './transaction.actions';
import { transactionFeature } from './transaction.feature';

@Component({
  selector: 'app-transaction-overview',
  imports: [RouterLink],
  template: `
    <main>
      <a routerLink="/">← All accounts</a>

      @if (loading()) {
        <p>Loading…</p>
      }
      @if (error()) {
        <p class="error">{{ error() }}</p>
      }
      @if (transaction(); as tx) {
        <h1>Transaction {{ tx.transactionId }}</h1>
        <p>Date: {{ tx.createdAt }}</p>
        <button type="button" (click)="downloadPdf()">Export PDF</button>
        <table>
          <thead>
            <tr>
              <th>Account</th>
              <th>Counterparty</th>
              <th>Type</th>
              <th>Amount</th>
              <th>Currency</th>
              <th>Description</th>
            </tr>
          </thead>
          <tbody>
            @for (leg of tx.legs; track $index) {
              <tr>
                <td>{{ leg.accountId }}</td>
                <td>{{ leg.counterpartyAccountId }}</td>
                <td>{{ leg.type }}</td>
                <td>{{ leg.amount }}</td>
                <td>{{ leg.currency }}</td>
                <td>{{ leg.description }}</td>
              </tr>
            }
          </tbody>
        </table>
      }
    </main>
  `,
})
export class TransactionOverview {
  readonly transactionId = input.required<string>();

  private readonly store = inject(Store);
  private readonly api = inject(TransactionApi);

  readonly transaction = this.store.selectSignal(transactionFeature.selectTransaction);
  readonly loading = this.store.selectSignal(transactionFeature.selectLoading);
  readonly error = this.store.selectSignal(transactionFeature.selectError);

  constructor() {
    effect(() => {
      this.store.dispatch(TransactionActions.loadTransaction({ transactionId: this.transactionId() }));
    });
  }

  downloadPdf(): void {
    const id = this.transactionId();
    this.api.getPdf(id).subscribe((blob) => {
      const url = URL.createObjectURL(blob);
      const anchor = document.createElement('a');
      anchor.href = url;
      anchor.download = `transaction-${id}.pdf`;
      anchor.click();
      URL.revokeObjectURL(url);
    });
  }
}
