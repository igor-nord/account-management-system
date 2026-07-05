import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { CurrentCustomer } from './current-customer';

export const usernameInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.startsWith('/api')) {
    const username = inject(CurrentCustomer).username();
    if (username !== null) {
      return next(req.clone({ setHeaders: { 'X-Username': username } }));
    }
  }
  return next(req);
};
