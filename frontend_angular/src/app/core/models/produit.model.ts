import { PageQuery } from './common.model';

/** Produit du catalogue textile (miroir de `ProduitResponse`). */
export interface Produit {
  id: number;
  code: string;
  designation: string;
  categorie: string | null;
  matiere: string | null;
  prixUnitaire: number;
  stock: number;
  active: boolean;
}

/** Corps de création / mise à jour d'un produit. */
export interface ProduitPayload {
  code?: string;
  designation: string;
  categorie?: string | null;
  matiere?: string | null;
  prixUnitaire: number;
  stock: number;
  active?: boolean;
}

/** Filtres de la liste des produits. */
export interface ProduitQuery extends PageQuery {
  search?: string;
  categorie?: string;
  active?: boolean;
}
