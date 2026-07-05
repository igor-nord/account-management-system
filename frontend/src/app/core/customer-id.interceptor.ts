import { HttpInterceptorFn } from '@angular/common/http';

export const customerIdInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.startsWith('/api')) {
    return next(req.clone({ setHeaders: { 'X-Customer-Id': '1' } }));
  }
  return next(req);
};
