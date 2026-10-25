import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environments';
import { PageResponse } from '../models/common.model';
import { Produit, ProduitPayload, ProduitQuery } from '../models/produit.model';
import { toHttpParams } from '../http/http-params';

/** Accès REST au catalogue produits. */
@Injectable({ providedIn: 'root' })
export class ProduitService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/produits`;

  list(query: ProduitQuery = {}): Observable<PageResponse<Produit>> {
    return this.http.get<PageResponse<Produit>>(this.baseUrl, {
      params: toHttpParams({ ...query }),
    });
  }

  categories(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/categories`);
  }

  getById(id: number): Observable<Produit> {
    return this.http.get<Produit>(`${this.baseUrl}/${id}`);
  }

  create(payload: ProduitPayload): Observable<Produit> {
    return this.http.post<Produit>(this.baseUrl, payload);
  }

  update(id: number, payload: Partial<ProduitPayload>): Observable<Produit> {
    return this.http.put<Produit>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
