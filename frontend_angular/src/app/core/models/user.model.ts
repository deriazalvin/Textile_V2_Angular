import { AppRole } from './enums';

/** Compte applicatif (miroir de l'entité `AppUser` exposée par /api/users). */
export interface AppUser {
  id: number;
  username: string;
  displayName: string;
  role: AppRole;
  enabled: boolean;
}

/** Corps de création d'un compte. */
export interface AppUserPayload {
  username: string;
  password: string;
  displayName?: string;
  role?: AppRole;
}
