import { Injectable, inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { AppRole } from '../models/enums';
import { AuthService } from '../services/auth.service';

/** Bloque l'accès aux routes protégées et mémorise l'URL de retour. */
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  canActivate(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): boolean | UrlTree {
    return this.auth.connecte()
      ? true
      : this.router.createUrlTree(['/login'], {
          queryParams: { retour: state.url },
        });
  }
}

/**
 * Autorise l'accès selon `data.roles` de la route.
 * Sans `roles`, la route est accessible à tout utilisateur connecté.
 */
@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    const roles = (route.data['roles'] ?? []) as AppRole[];
    if (roles.length === 0) {
      return true;
    }
    if (!this.auth.connecte()) {
      return this.router.createUrlTree(['/login']);
    }
    return this.auth.aRole(...roles)
      ? true
      : this.router.createUrlTree(['/dashboard']);
  }
}
