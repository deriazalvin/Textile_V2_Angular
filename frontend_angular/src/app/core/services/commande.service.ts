import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environments';
import { PageResponse } from '../models/common.model';
import {
  Commande,
  CommandePayload,
  CommandeQuery,
  CommandeUpdatePayload,
} from '../models/commande.model';
import { toHttpParams } from '../http/http-params';

/** Accès REST aux commandes. */
@Injectable({ providedIn: 'root' })
export class CommandeService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/orders`;

  list(query: CommandeQuery = {}): Observable<PageResponse<Commande>> {
    return this.http.get<PageResponse<Commande>>(this.baseUrl, {
      params: toHttpParams({ ...query }),
    });
  }

  /** Commandes actives triées par priorité puis urgence. */
  actives(): Observable<Commande[]> {
    return this.http.get<Commande[]>(`${this.baseUrl}/active`);
  }

  /** Commandes susceptibles d'être livrées en retard. */
  retards(): Observable<Commande[]> {
    return this.http.get<Commande[]>(`${this.baseUrl}/delayed`);
  }

  getById(id: number): Observable<Commande> {
    return this.http.get<Commande>(`${this.baseUrl}/${id}`);
  }

  getByNumero(numero: string): Observable<Commande> {
    return this.http.get<Commande>(`${this.baseUrl}/numero/${numero}`);
  }

  create(payload: CommandePayload): Observable<Commande> {
    return this.http.post<Commande>(this.baseUrl, payload);
  }

  update(id: number, payload: CommandeUpdatePayload): Observable<Commande> {
    return this.http.put<Commande>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
