import { Commande } from './commande.model';
import { StatutCommande } from './enums';

/** Indicateurs du tableau de bord (miroir de `DashboardResponse`). */
export interface DashboardStats {
  totalCommandes: number;
  commandesEnProduction: number;
  commandesEnRetard: number;
  commandesLivrees: number;
  commandesUrgent: number;
  chiffreAffairesLivree: number;
  tauxConformite: number;
  delaiMoyenLivraisonJours: number;
  repartitionParStatut: Partial<Record<StatutCommande, number>>;
  commandesPrioritaires: Commande[];
  commandesEnRetardListe: Commande[];
}

/** Statistiques de production (miroir de `ProductionStatisticsResponse`). */
export interface ProductionStats {
  totalCommandes: number;
  commandesLivrees: number;
  commandesEnCours: number;
  commandesAnnulees: number;
  quantiteCommandee: number;
  quantiteProduite: number;
  rendementGlobal: number;

  delaiMoyenJours: number;
  commandesLivreesEnRetard: number;
  commandesLivreesATemps: number;
  tauxLivraisonATemps: number;
  commandesEnRetard: number;

  controlesConformes: number;
  controlesNonConformes: number;
  tauxConformite: number;

  parStatut: StatutBucket[];
  parPriorite: PrioriteBucket[];
  parEtape: EtapeStat[];
  parProduit: ProduitRendement[];
  parClient: ClientVolume[];
}

export interface StatutBucket {
  statut: string;
  nombre: number;
}

export interface PrioriteBucket {
  priorite: string;
  nombre: number;
}

export interface EtapeStat {
  etape: string;
  rang: number;
  etapesTerminees: number;
  quantiteProduite: number;
  tauxReussite: number;
}

export interface ProduitRendement {
  code: string;
  designation: string;
  quantiteCommandee: number;
  quantiteProduite: number;
  ecart: number;
  rendement: number;
}

export interface ClientVolume {
  client: string;
  commandes: number;
  montant: number;
}
