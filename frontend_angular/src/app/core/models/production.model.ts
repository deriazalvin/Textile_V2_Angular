import { ControleQualite, EtapeProduction, LigneCommande } from './commande.model';
import { ResultatControle, TypeEtape } from './enums';

/** Vue « production » d'une commande (miroir de `ProductionResponse`). */
export interface Production {
  commandeId: number;
  numero: string;
  etapeCourante: TypeEtape | null;
  etapeCouranteRang: number;
  etapesTerminees: number;
  etapesTotal: number;
  avancementPourcentage: number;
  quantiteCommandee: number;
  quantiteProduite: number;
  dateLivraisonPrevue: string;
  retardJours: number | null;
  enRetard: boolean;
  lignes: LigneCommande[];
  etapes: EtapeProduction[];
  controleQualite: ControleQualite | null;
}

/** Démarrage de la production. */
export interface StartProductionPayload {
  responsableId?: number | null;
  commentaire?: string | null;
}

/** Quantité produite pour une ligne lors de la clôture d'une étape. */
export interface StepLineQuantity {
  ligneId: number;
  quantite: number;
}

/** Clôture d'une étape de production. */
export interface CompleteStepPayload {
  quantites: StepLineQuantity[];
  commentaire?: string | null;
}

/** Saisie du contrôle qualité. */
export interface QualityControlPayload {
  resultat: ResultatControle;
  controleurId?: number | null;
  observations?: string | null;
  pointsNonConformites?: string | null;
}

/** Preuve de livraison. */
export interface DeliveryPayload {
  receptionnaire: string;
  bonLivraison?: string | null;
  commentaire?: string | null;
}

/** Livraison enregistrée (miroir de `DeliveryResponse`). */
export interface Livraison {
  id: number;
  numero: string;
  dateLivraison: string;
  receptionnaire: string;
  bonLivraison: string | null;
  commentaire: string | null;
  quantiteLivree: number | null;
  retardJours: number | null;
}
