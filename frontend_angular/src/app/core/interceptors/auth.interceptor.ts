import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environments';
import { AuthStorageService } from '../services/auth-storage.service';

/**
 * Ajoute l'en-tête `Authorization: Basic …` aux appels de l'API.
 * Les requêtes externes (polices, etc.) ne sont pas concernées.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private readonly storage = inject(AuthStorageService);

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler,
  ): Observable<HttpEvent<unknown>> {
    const token = this.storage.token();

    if (token && req.url.startsWith(environment.apiUrl)) {
      return next.handle(
        req.clone({ setHeaders: { Authorization: `Basic ${token}` } }),
      );
    }
    return next.handle(req);
  }
}
