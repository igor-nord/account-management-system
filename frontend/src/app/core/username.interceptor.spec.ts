import { describe, it, expect } from 'vitest';
import { HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { Injector, runInInjectionContext } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { usernameInterceptor } from './username.interceptor';
import { CurrentCustomer } from './current-customer';

function capture(url: string, username: string | null): HttpRequest<unknown> {
  TestBed.resetTestingModule();
  TestBed.configureTestingModule({
    providers: [{ provide: CurrentCustomer, useValue: { username: () => username } }],
  });
  let captured!: HttpRequest<unknown>;
  const next: HttpHandlerFn = (req) => {
    captured = req;
    return of();
  };
  runInInjectionContext(TestBed.inject(Injector), () =>
    usernameInterceptor(new HttpRequest('GET', url), next).subscribe(),
  );
  return captured;
}

describe('usernameInterceptor', () => {
  it('adds the current X-Username to /api requests', () => {
    expect(capture('/api/accounts', 'demo').headers.get('X-Username')).toBe('demo');
  });

  it('omits the header when no customer is selected', () => {
    expect(capture('/api/accounts', null).headers.get('X-Username')).toBeNull();
  });

  it('leaves non-api requests untouched', () => {
    expect(capture('/assets/logo.png', 'demo').headers.get('X-Username')).toBeNull();
  });
});
