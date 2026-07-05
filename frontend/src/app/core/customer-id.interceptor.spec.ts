import { describe, it, expect } from 'vitest';
import { HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { of } from 'rxjs';
import { customerIdInterceptor } from './customer-id.interceptor';

function capture(url: string): HttpRequest<unknown> {
  let captured!: HttpRequest<unknown>;
  const next: HttpHandlerFn = (req) => {
    captured = req;
    return of();
  };
  customerIdInterceptor(new HttpRequest('GET', url), next).subscribe();
  return captured;
}

describe('customerIdInterceptor', () => {
  it('adds X-Customer-Id to /api requests', () => {
    expect(capture('/api/accounts').headers.get('X-Customer-Id')).toBe('1');
  });

  it('leaves non-api requests untouched', () => {
    expect(capture('/assets/logo.png').headers.get('X-Customer-Id')).toBeNull();
  });
});
