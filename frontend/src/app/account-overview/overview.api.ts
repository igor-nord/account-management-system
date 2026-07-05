import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { BalanceSeriesResponse, HistoryPageResponse } from './overview.models';

@Injectable({ providedIn: 'root' })
export class OverviewApi {
  private readonly http = inject(HttpClient);

  getHistory(accountId: number, limit: number, cursor?: string): Observable<HistoryPageResponse> {
    let params = new HttpParams().set('limit', limit);
    if (cursor) {
      params = params.set('cursor', cursor);
    }
    return this.http.get<HistoryPageResponse>('/api/account/transactions', {
      params,
      headers: { 'X-Account-Id': String(accountId) },
    });
  }

  getBalanceSeries(accountId: number): Observable<BalanceSeriesResponse> {
    return this.http.get<BalanceSeriesResponse>('/api/account/balance-series', {
      headers: { 'X-Account-Id': String(accountId) },
    });
  }

  credit(accountId: number, amount: string, description: string): Observable<unknown> {
    return this.http.post('/api/account/credit', { amount, description }, {
      headers: { 'X-Account-Id': String(accountId) },
    });
  }

  debit(accountId: number, amount: string, description: string): Observable<unknown> {
    return this.http.post('/api/account/debit', { amount, description }, {
      headers: { 'X-Account-Id': String(accountId) },
    });
  }

  exchange(sourceAccountId: number, targetAccountId: number, amount: string): Observable<unknown> {
    return this.http.post('/api/exchange', { amount }, {
      headers: {
        'X-Source-Account-Id': String(sourceAccountId),
        'X-Target-Account-Id': String(targetAccountId),
      },
    });
  }
}
