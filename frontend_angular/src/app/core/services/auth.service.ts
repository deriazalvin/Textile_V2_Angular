import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, switchMap, tap } from 'rxjs';
import { environment } from '../../../environments/environments';
import { AuthUser, LoginCredentials, RegisterPayload } from '../models/auth.model';
import { AppRole } from '../models/enums';
import { AuthStorageService } from './auth-storage.service';

/**
 * Authentification HTTP Basic de l'application.
 *
 * <p>Le backend Spring Boot protège `/api/**` par HTTP Basic. Les identifiants
 * sont encodés une seule fois puis conservés dans `sessionStorage` ; l'en-tête
 * `Authorization` est ajouté à chaque requête par `AuthInterceptor`.</p>
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly storage = inject(AuthStorageService);
  private readonly router = inject(Router);

  private readonly _utilisateur = signal<AuthUser | null>(null);

  /** Utilisateur courant (réactif). */
  readonly utilisateur = this._utilisateur.asReadonly();
  /** Vrai si une session est active. */
  readonly connecte = computed(() => this._utilisateur() !== null);
  /** Rôle de l'utilisateur courant, ou `null`. */
  readonly role = computed<AppRole | null>(() => this._utilisateur()?.role ?? null);

  /**
   * Restaure la session au démarrage, puis la revalide en arrière-plan.
   * Appelée par `provideAppInitializer` avant l'activation des routes.
   */
  restaurer(): void {
    const session = this.storage.lire();
    if (!session) {
      return;
    }
    this._utilisateur.set(session.user);
    this.rafraichir();
  }

  /** Vérifie les identifiants et mémorise la session. */
  connecter(identifiants: LoginCredentials): Observable<AuthUser> {
    const username = identifiants.username.trim();
    const token = encoderBasic(username, identifiants.password);

    return this.http
      .get<AuthUser>(`${environment.apiUrl}/auth/me`, {
        headers: { Authorization: `Basic ${token}` },
      })
      .pipe(
        tap((utilisateur) => {
          this.storage.ecrire({ token, user: utilisateur });
          this._utilisateur.set(utilisateur);
        }),
      );
  }

  /** Crée un compte de consultation puis connecte automatiquement. */
  inscrire(payload: RegisterPayload): Observable<AuthUser> {
    return this.http
      .post<AuthUser>(`${environment.apiUrl}/auth/register`, payload)
      .pipe(
        switchMap(() =>
          this.connecter({
            username: payload.username,
            password: payload.password,
          }),
        ),
      );
  }

  /** Ferme la session et redirige vers la page de connexion. */
  deconnecter(rediriger = true): void {
    this.storage.effacer();
    this._utilisateur.set(null);
    if (rediriger) {
      void this.router.navigate(['/login'], { queryParams: { deconnexion: '1' } });
    }
  }

  /** Vrai si l'utilisateur possède l'un des rôles demandés. */
  aRole(...roles: AppRole[]): boolean {
    const role = this.role();
    return role !== null && roles.includes(role);
  }

  /** Revalide la session auprès du backend (rôle à jour, compte désactivé…). */
  private rafraichir(): void {
    const token = this.storage.token();
    if (!token) {
      return;
    }
    this.http.get<AuthUser>(`${environment.apiUrl}/auth/me`).subscribe({
      next: (utilisateur) => {
        this.storage.ecrire({ token, user: utilisateur });
        this._utilisateur.set(utilisateur);
      },
      error: (erreur: HttpErrorResponse) => {
        if (erreur.status === 401) {
          this.deconnecter();
        }
      },
    });
  }
}

/** Encode `username:password` en base64 (UTF-8 compatible). */
function encoderBasic(username: string, password: string): string {
  if (typeof btoa !== 'function') {
    return '';
  }
  const octets = new TextEncoder().encode(`${username}:${password}`);
  let binaire = '';
  octets.forEach((octet) => {
    binaire += String.fromCharCode(octet);
  });
  return btoa(binaire);
}
