import { PageQuery } from './common.model';
import {
  PrioriteCommande,
  ResultatControle,
  StatutCommande,
  StatutEtape,
  TypeEtape,
} from './enums';

/** Ligne d'une commande (miroir de `OrderLineResponse`). */
export interface LigneCommande {
  id: number;
  produitId: number;
  produitCode: string;
  produitDesignation: string;
  quantite: number;
  taille: string | null;
  couleur: string | null;
  prixUnitaire: number;
  montant: number;
  quantiteProduite: number;
  quantiteRestante: number;
}

/** Étape du workflow d'une commande (miroir de `StepResponse`). */
export interface EtapeProduction {
  id: number;
  type: TypeEtape;
  typeLibelle: string;
  rang: number;
  statut: StatutEtape;
  responsableId: number | null;
  responsableNom: string | null;
  dateDebut: string | null;
  dateFinPrevue: string | null;
  dateFinReelle: string | null;
  commentaire: string | null;
  quantiteProduite: number | null;
}

/** Contrôle qualité d'une commande (miroir de `QualityControlResponse`). */
export interface ControleQualite {
  id: number;
  resultat: ResultatControle;
  controleurNom: string | null;
  dateControle: string;
  observations: string | null;
  pointsNonConformites: string | null;
  quantiteControlee: number | null;
}

/** Commande complète (miroir de `OrderResponse`). */
export interface Commande {
  id: number;
  numero: string;
  clientId: number;
  clientNom: string;
  clientTelephone: string | null;
  dateCommande: string;
  dateLivraisonPrevue: string;
  dateLivraisonReelle: string | null;
  priorite: PrioriteCommande;
  statut: StatutCommande;
  commentaire: string | null;
  instructionsSpeciales: string | null;
  montantTotal: number;
  quantiteTotale: number;
  quantiteProduiteTotale: number;
  dureePrevueJours: number | null;
  dureeReelleJours: number | null;
  retardJours: number | null;
  enRetard: boolean;
  etapeCourante: TypeEtape | null;
  lignes: LigneCommande[];
  etapes: EtapeProduction[];
  controleQualite: ControleQualite | null;
}

/** Filtres de la liste des commandes. */
export interface CommandeQuery extends PageQuery {
  search?: string;
  statut?: StatutCommande;
  priorite?: PrioriteCommande;
  clientId?: number;
  delayed?: boolean;
}

/** Ligne envoyée lors de la création / mise à jour d'une commande. */
export interface LigneCommandePayload {
  produitId: number;
  quantite: number;
  taille?: string | null;
  couleur?: string | null;
  prixUnitaire?: number | null;
}

/** Corps de création d'une commande. */
export interface CommandePayload {
  clientId: number;
  dateLivraisonPrevue: string;
  priorite: PrioriteCommande;
  commentaire?: string | null;
  instructionsSpeciales?: string | null;
  lignes: LigneCommandePayload[];
}

/** Corps de mise à jour partielle d'une commande. */
export type CommandeUpdatePayload = Partial<CommandePayload>;
