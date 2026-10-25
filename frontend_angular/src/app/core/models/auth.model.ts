import { AppRole } from './enums';

/** Utilisateur authentifié renvoyé par `GET /api/auth/me`. */
export interface AuthUser {
  username: string;
  displayName: string;
  role: AppRole;
}

/** Identifiants du formulaire de connexion. */
export interface LoginCredentials {
  username: string;
  password: string;
}

/** Corps d'inscription (compte de consultation). */
export interface RegisterPayload {
  username: string;
  password: string;
  displayName?: string;
}
