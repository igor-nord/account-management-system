import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CustomerResponse } from './customer.models';

@Injectable({ providedIn: 'root' })
export class CustomerApi {
  private readonly http = inject(HttpClient);

  findByUsername(username: string): Observable<CustomerResponse> {
    return this.http.get<CustomerResponse>('/api/customer', {
      headers: { 'X-Username': username },
    });
  }
}
