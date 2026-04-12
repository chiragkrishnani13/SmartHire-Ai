import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    const creds = this.auth.getCredentials();
    if (creds) {
      const cloned = req.clone({
        setHeaders: { Authorization: `Basic ${creds}` }
      });
      return next.handle(cloned);
    }
    return next.handle(req);
  }
}
