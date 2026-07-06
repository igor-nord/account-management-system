import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { AccountSummary } from './account.models';

@Injectable({ providedIn: 'root' })
export class AccountApi {
  private readonly http = inject(HttpClient);

  getAccounts(): Observable<AccountSummary[]> {
    return this.http.get<AccountSummary[]>('/api/accounts');
  }

  getAccount(accountCode: number): Observable<AccountSummary> {
    return this.http.get<AccountSummary>('/api/account', {
      headers: { 'X-Account-Code': String(accountCode) },
    });
  }
}
