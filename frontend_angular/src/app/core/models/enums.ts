/** Statuts d'une commande (voir `StatutCommande` côté backend). */
export type StatutCommande =
  | 'BROUILLON'
  | 'PLANIFIEE'
  | 'EN_PRODUCTION'
  | 'EN_CONTROLE'
  | 'LIVREE'
  | 'ANNULEE';

/** Priorités d'une commande. */
export type PrioriteCommande = 'NORMALE' | 'URGENTE' | 'TRES_URGENTE';

/** Étapes du workflow, dans leur ordre strict. */
export type TypeEtape =
  | 'COMMANDE'
  | 'COUPE'
  | 'COUTURE'
  | 'FINITION'
  | 'CONTROLE_QUALITE'
  | 'LIVRAISON';

/** Statut d'une étape de production. */
export type StatutEtape = 'NON_COMMENCEE' | 'EN_COURS' | 'TERMINEE' | 'BLOQUEE';

/** Résultat du contrôle qualité. */
export type ResultatControle = 'CONFORME' | 'NON_CONFORME';

/** Rôles applicatifs. */
export type AppRole = 'ROLE_ADMIN' | 'ROLE_MANAGER' | 'ROLE_OPERATOR' | 'ROLE_USER';

export const STATUTS_COMMANDE: readonly StatutCommande[] = [
  'BROUILLON',
  'PLANIFIEE',
  'EN_PRODUCTION',
  'EN_CONTROLE',
  'LIVREE',
  'ANNULEE',
];

export const PRIORITES_COMMANDE: readonly PrioriteCommande[] = [
  'NORMALE',
  'URGENTE',
  'TRES_URGENTE',
];

export const TYPES_ETAPE: readonly TypeEtape[] = [
  'COMMANDE',
  'COUPE',
  'COUTURE',
  'FINITION',
  'CONTROLE_QUALITE',
  'LIVRAISON',
];

export const LIBELLES_STATUT: Record<StatutCommande, string> = {
  BROUILLON: 'Brouillon',
  PLANIFIEE: 'Planifiée',
  EN_PRODUCTION: 'En production',
  EN_CONTROLE: 'En contrôle',
  LIVREE: 'Livrée',
  ANNULEE: 'Annulée',
};

export const LIBELLES_PRIORITE: Record<PrioriteCommande, string> = {
  NORMALE: 'Normale',
  URGENTE: 'Urgente',
  TRES_URGENTE: 'Très urgente',
};

export const LIBELLES_ETAPE: Record<TypeEtape, string> = {
  COMMANDE: 'Commande',
  COUPE: 'Coupe',
  COUTURE: 'Couture',
  FINITION: 'Finition',
  CONTROLE_QUALITE: 'Contrôle qualité',
  LIVRAISON: 'Livraison',
};

export const LIBELLES_STATUT_ETAPE: Record<StatutEtape, string> = {
  NON_COMMENCEE: 'Non commencée',
  EN_COURS: 'En cours',
  TERMINEE: 'Terminée',
  BLOQUEE: 'Bloquée',
};

export const LIBELLES_ROLE: Record<AppRole, string> = {
  ROLE_ADMIN: 'Administrateur',
  ROLE_MANAGER: 'Manager',
  ROLE_OPERATOR: 'Opérateur',
  ROLE_USER: 'Consultation',
};

/** Classe de badge associée à un statut de commande. */
export function badgeStatut(statut: StatutCommande): string {
  switch (statut) {
    case 'LIVREE':
      return 'badge--success';
    case 'ANNULEE':
      return 'badge--danger';
    case 'EN_PRODUCTION':
    case 'EN_CONTROLE':
      return 'badge--warning';
    default:
      return 'badge--info';
  }
}

/** Classe de badge associée à une priorité. */
export function badgePriorite(priorite: PrioriteCommande): string {
  switch (priorite) {
    case 'TRES_URGENTE':
      return 'badge--danger';
    case 'URGENTE':
      return 'badge--warning';
    default:
      return 'badge--muted';
  }
}

/** Classe de badge associée au statut d'une étape. */
export function badgeStatutEtape(statut: StatutEtape): string {
  switch (statut) {
    case 'TERMINEE':
      return 'badge--success';
    case 'EN_COURS':
      return 'badge--warning';
    case 'BLOQUEE':
      return 'badge--danger';
    default:
      return 'badge--muted';
  }
}
