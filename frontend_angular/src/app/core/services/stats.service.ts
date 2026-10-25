import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environments';
import { DashboardStats, ProductionStats } from '../models/stats.model';

/** Accès REST aux indicateurs du tableau de bord et aux statistiques. */
@Injectable({ providedIn: 'root' })
export class StatsService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/stats`;

  dashboard(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.baseUrl}/dashboard`);
  }

  production(): Observable<ProductionStats> {
    return this.http.get<ProductionStats>(`${this.baseUrl}/production`);
  }
}
