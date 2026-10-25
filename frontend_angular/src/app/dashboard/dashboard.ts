import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import {
  LIBELLES_ETAPE,
  LIBELLES_PRIORITE,
  LIBELLES_STATUT,
  STATUTS_COMMANDE,
  StatutCommande,
  TypeEtape,
  badgePriorite,
  badgeStatut,
} from '../core/models/enums';
import { DashboardStats } from '../core/models/stats.model';
import { AuthService } from '../core/services/auth.service';
import { StatsService } from '../core/services/stats.service';

interface Indicateur {
  libelle: string;
  valeur: string;
  icone: string;
  classe?: string;
}

interface RepartitionStatut {
  statut: StatutCommande;
  libelle: string;
  badge: string;
  nombre: number;
}

/** Icônes SVG (attribut `d`) des cartes d'indicateurs. */
const ICONES = {
  commandes:
    'M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z M3 6h18',
  production:
    'M12 2v4M12 18v4M4.9 4.9l2.8 2.8M16.3 16.3l2.8 2.8M2 12h4M18 12h4M4.9 19.1l2.8-2.8M16.3 7.7l2.8-2.8 M12 9a3 3 0 1 0 0 6 3 3 0 0 0 0-6',
  retard: 'M12 8v4l3 2M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18z',
  urgence: 'm13 2-9 12h7l-1 8 9-12h-7l1-8Z',
  livraison: 'M20 6 9 17l-5-5',
  ca: 'M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6',
  qualite:
    'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10Z M9 12l2 2 4-4',
  delai: 'M3 4h18v17H3zM3 10h18M8 2v4M16 2v4',
};

/**
 * Tableau de bord : indicateurs clés, répartition des statuts et listes
 * des commandes urgentes / en retard.
 */
@Component({
  standalone: false,
  selector: 'app-dashboard',
  styleUrl: './dashboard.scss',
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private readonly statsService = inject(StatsService);
  private readonly auth = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);

  readonly stats = signal<DashboardStats | null>(null);
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);

  readonly peutGerer = computed(() =>
    this.auth.aRole('ROLE_ADMIN', 'ROLE_MANAGER'),
  );

  readonly libellesPriorite = LIBELLES_PRIORITE;
  readonly libellesEtape = LIBELLES_ETAPE;
  readonly badgePriorite = badgePriorite;
  readonly badgeStatut = badgeStatut;

  readonly indicateurs = computed<Indicateur[]>(() => {
    const donnees = this.stats();
    if (!donnees) {
      return [];
    }
    return [
      {
        libelle: 'Commandes totales',
        valeur: this.nombre(donnees.totalCommandes),
        icone: ICONES.commandes,
      },
      {
        libelle: 'En production',
        valeur: this.nombre(donnees.commandesEnProduction),
        icone: ICONES.production,
      },
      {
        libelle: 'En retard',
        valeur: this.nombre(donnees.commandesEnRetard),
        icone: ICONES.retard,
        classe: 'kpi--danger',
      },
      {
        libelle: 'Urgentes',
        valeur: this.nombre(donnees.commandesUrgent),
        icone: ICONES.urgence,
        classe: 'kpi--warning',
      },
      {
        libelle: 'Livrées',
        valeur: this.nombre(donnees.commandesLivrees),
        icone: ICONES.livraison,
        classe: 'kpi--success',
      },
      {
        libelle: 'CA livré',
        valeur: this.montant(donnees.chiffreAffairesLivree),
        icone: ICONES.ca,
        classe: 'kpi--success',
      },
      {
        libelle: 'Conformité qualité',
        valeur: this.pourcentageTexte(donnees.tauxConformite),
        icone: ICONES.qualite,
      },
      {
        libelle: 'Délai moyen',
        valeur: this.jours(donnees.delaiMoyenLivraisonJours),
        icone: ICONES.delai,
      },
    ];
  });

  readonly repartition = computed<RepartitionStatut[]>(() => {
    const donnees = this.stats();
    if (!donnees) {
      return [];
    }
    return STATUTS_COMMANDE.map((statut) => ({
      statut,
      libelle: LIBELLES_STATUT[statut],
      badge: badgeStatut(statut),
      nombre: donnees.repartitionParStatut[statut] ?? 0,
    }));
  });

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);

    this.statsService
      .dashboard()
      .pipe(
        finalize(() => this.chargement.set(false)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (donnees) => this.stats.set(donnees),
        error: (erreur: HttpErrorResponse) => {
          this.erreur.set(
            erreur.status === 401
              ? 'Session expirée. Reconnectez-vous.'
              : 'Impossible de charger le tableau de bord. Vérifiez que le serveur est démarré.',
          );
        },
      });
  }

  /** Largeur en pourcentage, bornée entre 0 et 100. */
  pourcentage(valeur: number, total: number): number {
    if (total <= 0) {
      return 0;
    }
    return Math.min(100, Math.max(0, Math.round((valeur * 100) / total)));
  }

  libelleEtape(etape: TypeEtape | null): string {
    return etape ? LIBELLES_ETAPE[etape] : 'Terminée';
  }

  private nombre(valeur: number): string {
    return new Intl.NumberFormat('fr-FR', {
      maximumFractionDigits: 2,
    }).format(valeur);
  }

  private montant(valeur: number): string {
    return `${this.nombre(valeur)} DT`;
  }

  private pourcentageTexte(valeur: number): string {
    return `${this.nombre(valeur)} %`;
  }

  private jours(valeur: number): string {
    return `${this.nombre(valeur)} j`;
  }
}
