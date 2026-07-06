import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { BalanceSeriesResponse, HistoryPageResponse } from './overview.models';

@Injectable({ providedIn: 'root' })
export class OverviewApi {
  private readonly http = inject(HttpClient);

  getHistory(accountCode: number, limit: number, cursor?: string): Observable<HistoryPageResponse> {
    let params = new HttpParams().set('limit', limit);
    if (cursor) {
      params = params.set('cursor', cursor);
    }
    return this.http.get<HistoryPageResponse>('/api/account/transactions', {
      params,
      headers: { 'X-Account-Code': String(accountCode) },
    });
  }

  getBalanceSeries(accountCode: number): Observable<BalanceSeriesResponse> {
    return this.http.get<BalanceSeriesResponse>('/api/account/balance-series', {
      headers: { 'X-Account-Code': String(accountCode) },
    });
  }

  credit(accountCode: number, amount: string, description: string): Observable<unknown> {
    return this.http.post('/api/account/credit', { amount, description }, {
      headers: { 'X-Account-Code': String(accountCode) },
    });
  }

  debit(accountCode: number, amount: string, description: string): Observable<unknown> {
    return this.http.post('/api/account/debit', { amount, description }, {
      headers: { 'X-Account-Code': String(accountCode) },
    });
  }

  exchange(sourceAccountCode: number, targetAccountCode: number, amount: string): Observable<unknown> {
    return this.http.post('/api/exchange', { amount }, {
      headers: {
        'X-Source-Account-Code': String(sourceAccountCode),
        'X-Target-Account-Code': String(targetAccountCode),
      },
    });
  }
}
