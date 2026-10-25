import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environments';
import { Commande } from '../models/commande.model';
import { TypeEtape } from '../models/enums';
import {
  CompleteStepPayload,
  DeliveryPayload,
  Livraison,
  Production,
  QualityControlPayload,
  StartProductionPayload,
} from '../models/production.model';
import { toHttpParams } from '../http/http-params';

/** Accès REST au workflow de production, au contrôle qualité et à la livraison. */
@Injectable({ providedIn: 'root' })
export class ProductionService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/orders`;

  /** Démarre la production : crée les étapes du workflow. */
  demarrerProduction(id: number, payload: StartProductionPayload = {}): Observable<Commande> {
    return this.http.post<Commande>(`${this.baseUrl}/${id}/production/start`, payload);
  }

  /** Démarre une étape précise. */
  demarrerEtape(id: number, etape: TypeEtape, commentaire?: string): Observable<Commande> {
    return this.http.post<Commande>(
      `${this.baseUrl}/${id}/production/steps/${etape}/start`,
      null,
      { params: toHttpParams({ commentaire }) },
    );
  }

  /** Clôture une étape en déclarant les quantités produites. */
  cloturerEtape(
    id: number,
    etape: TypeEtape,
    payload: CompleteStepPayload,
  ): Observable<Commande> {
    return this.http.post<Commande>(
      `${this.baseUrl}/${id}/production/steps/${etape}/complete`,
      payload,
    );
  }

  /** État détaillé du workflow. */
  getProduction(id: number): Observable<Production> {
    return this.http.get<Production>(`${this.baseUrl}/${id}/production`);
  }

  /** Enregistre le contrôle qualité (obligatoire avant livraison). */
  controleQualite(id: number, payload: QualityControlPayload): Observable<Commande> {
    return this.http.post<Commande>(`${this.baseUrl}/${id}/quality-control`, payload);
  }

  /** Enregistre la livraison (uniquement après contrôle CONFORME). */
  livrer(id: number, payload: DeliveryPayload): Observable<Livraison> {
    return this.http.post<Livraison>(`${this.baseUrl}/${id}/delivery`, payload);
  }

  /** Annule une commande. */
  annuler(id: number, motif?: string): Observable<Commande> {
    return this.http.post<Commande>(`${this.baseUrl}/${id}/cancel`, null, {
      params: toHttpParams({ motif }),
    });
  }
}
