import { PageQuery } from './common.model';

/** Client du référentiel (miroir de `ClientResponse`). */
export interface Client {
  id: number;
  name: string;
  phone: string;
  email: string | null;
  address: string | null;
  city: string | null;
  country: string | null;
  notes: string | null;
  active: boolean;
  createdAt: string;
  nombreCommandes: number;
}

/** Corps de création / mise à jour d'un client. */
export interface ClientPayload {
  name: string;
  phone: string;
  email?: string | null;
  address?: string | null;
  city?: string | null;
  notes?: string | null;
  active?: boolean;
}

/** Filtres de la liste des clients. */
export interface ClientQuery extends PageQuery {
  search?: string;
  active?: boolean;
}
