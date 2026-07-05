import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { TransactionResponse } from './transaction.models';

@Injectable({ providedIn: 'root' })
export class TransactionApi {
  private readonly http = inject(HttpClient);

  getTransaction(transactionId: string): Observable<TransactionResponse> {
    return this.http.get<TransactionResponse>('/api/transaction', {
      headers: { 'X-Transaction-Id': transactionId },
    });
  }

  getPdf(transactionId: string): Observable<Blob> {
    return this.http.get('/api/transaction/pdf', {
      headers: { 'X-Transaction-Id': transactionId },
      responseType: 'blob',
    });
  }
}
