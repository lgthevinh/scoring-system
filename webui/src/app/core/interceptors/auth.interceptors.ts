import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Get the auth token from local storage.
  const authToken = localStorage.getItem('authToken');

  // If a token exists, clone the request and add the Authorization header.
  if (authToken) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${authToken}`
      }
    });
    return next(cloned);
  }

  // If no token, pass the original request along.
  return next(req);
};
