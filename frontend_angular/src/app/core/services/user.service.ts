import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environments';
import { SpringPage } from '../models/common.model';
import { AppUser, AppUserPayload } from '../models/user.model';
import { toHttpParams } from '../http/http-params';

/** Accès REST à l'administration des comptes (ROLE_ADMIN). */
@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/users`;

  list(page = 0, size = 20): Observable<SpringPage<AppUser>> {
    return this.http.get<SpringPage<AppUser>>(this.baseUrl, {
      params: toHttpParams({ page, size }),
    });
  }

  create(payload: AppUserPayload): Observable<AppUser> {
    return this.http.post<AppUser>(this.baseUrl, payload);
  }

  activer(id: number, enabled: boolean): Observable<{ id: number; enabled: boolean }> {
    return this.http.put<{ id: number; enabled: boolean }>(
      `${this.baseUrl}/${id}/enabled`,
      null,
      { params: toHttpParams({ enabled }) },
    );
  }
}
