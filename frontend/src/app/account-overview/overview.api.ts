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
}
