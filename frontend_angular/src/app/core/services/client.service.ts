import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environments';
import { PageResponse } from '../models/common.model';
import { Client, ClientPayload, ClientQuery } from '../models/client.model';
import { toHttpParams } from '../http/http-params';

/** Accès REST au référentiel clients. */
@Injectable({ providedIn: 'root' })
export class ClientService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/clients`;

  list(query: ClientQuery = {}): Observable<PageResponse<Client>> {
    return this.http.get<PageResponse<Client>>(this.baseUrl, {
      params: toHttpParams({ ...query }),
    });
  }

  getById(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.baseUrl}/${id}`);
  }

  create(payload: ClientPayload): Observable<Client> {
    return this.http.post<Client>(this.baseUrl, payload);
  }

  update(id: number, payload: Partial<ClientPayload>): Observable<Client> {
    return this.http.put<Client>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
