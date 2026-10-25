import { isPlatformBrowser } from '@angular/common';
import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { AuthUser } from '../models/auth.model';

const CLE_SESSION = 'textile.session';

/** Session persistée dans le navigateur (onglet courant uniquement). */
export interface SessionAuth {
  /** En-tête HTTP Basic pré-encodé (`base64(username:password)`). */
  token: string;
  user: AuthUser;
}

/**
 * Stockage de la session d'authentification.
 *
 * <p>Service volontairement isolé : l'intercepteur HTTP peut le lire sans
 * injecter `AuthService` (qui dépend lui-même de `HttpClient`), ce qui évite
 * une dépendance circulaire.</p>
 */
@Injectable({ providedIn: 'root' })
export class AuthStorageService {
  private readonly estNavigateur = isPlatformBrowser(inject(PLATFORM_ID));

  /** Repli mémoire pour le rendu serveur (pas de `sessionStorage`). */
  private memoire: SessionAuth | null = null;

  lire(): SessionAuth | null {
    if (!this.estNavigateur) {
      return this.memoire;
    }
    try {
      const brut = sessionStorage.getItem(CLE_SESSION);
      return brut ? (JSON.parse(brut) as SessionAuth) : null;
    } catch {
      return null;
    }
  }

  ecrire(session: SessionAuth): void {
    this.memoire = session;
    if (this.estNavigateur) {
      sessionStorage.setItem(CLE_SESSION, JSON.stringify(session));
    }
  }

  effacer(): void {
    this.memoire = null;
    if (this.estNavigateur) {
      sessionStorage.removeItem(CLE_SESSION);
    }
  }

  token(): string | null {
    return this.lire()?.token ?? null;
  }
}
