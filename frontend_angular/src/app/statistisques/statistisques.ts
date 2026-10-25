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
  LIBELLES_PRIORITE,
  LIBELLES_STATUT,
  PrioriteCommande,
  StatutCommande,
  badgePriorite,
  badgeStatut,
} from '../core/models/enums';
import { EtapeStat, ProductionStats } from '../core/models/stats.model';
import { StatsService } from '../core/services/stats.service';

interface Indicateur {
  libelle: string;
  valeur: string;
  icone: string;
  classe?: string;
}

interface EtapeAvecPerte extends EtapeStat {
  perte: number | null;
}

interface RepartitionStatut {
  statut: string;
  libelle: string;
  badge: string;
  nombre: number;
}

interface RepartitionPriorite {
  priorite: string;
  libelle: string;
  badge: string;
  nombre: number;
}

/** Icônes SVG (attribut `d`) des cartes d'indicateurs. */
const ICONES = {
  commandes:
    'M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z M3 6h18',
  livraison: 'M20 6 9 17l-5-5',
  production:
    'M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16zM3.27 6.96 12 12.01l8.73-5.05M12 22.08V12',
  retard: 'M12 8v4l3 2M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18z',
  quantite: 'M3 3h18v18H3zM3 9h18M9 21V9',
  rendement: 'M3 3v18h18M7 15v3M12 9v9M17 5v13',
  ponctualite: 'M12 8v4l3 2M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18z',
  qualite:
    'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10Z M9 12l2 2 4-4',
};

/** Statistiques de production : rendements, délais et qualité. */
@Component({
  standalone: false,
  selector: 'app-statistisques',
  styleUrl: './statistisques.scss',
  templateUrl: './statistisques.html',
})
export class Statistisques implements OnInit {
  private readonly statsService = inject(StatsService);
  private readonly destroyRef = inject(DestroyRef);

  readonly stats = signal<ProductionStats | null>(null);
  readonly chargement = signal(true);
  readonly erreur = signal<string | null>(null);

  readonly indicateurs = computed<Indicateur[]>(() => {
    const donnees = this.stats();
    if (!donnees) {
      return [];
    }
    return [
      {
        libelle: 'Commandes',
        valeur: this.nombre(donnees.totalCommandes),
        icone: ICONES.commandes,
      },
      {
        libelle: 'Livrées',
        valeur: this.nombre(donnees.commandesLivrees),
        icone: ICONES.livraison,
        classe: 'kpi--success',
      },
      {
        libelle: 'En cours',
        valeur: this.nombre(donnees.commandesEnCours),
        icone: ICONES.production,
        classe: 'kpi--warning',
      },
      {
        libelle: 'En retard',
        valeur: this.nombre(donnees.commandesEnRetard),
        icone: ICONES.retard,
        classe: 'kpi--danger',
      },
      {
        libelle: 'Quantité commandée',
        valeur: this.nombre(donnees.quantiteCommandee),
        icone: ICONES.quantite,
      },
      {
        libelle: 'Quantité produite',
        valeur: this.nombre(donnees.quantiteProduite),
        icone: ICONES.production,
      },
      {
        libelle: 'Rendement global',
        valeur: this.pourcentageTexte(donnees.rendementGlobal),
        icone: ICONES.rendement,
        classe: 'kpi--success',
      },
      {
        libelle: 'Livrées à temps',
        valeur: this.pourcentageTexte(donnees.tauxLivraisonATemps),
        icone: ICONES.ponctualite,
        classe: 'kpi--success',
      },
      {
        libelle: 'Conformité qualité',
        valeur: this.pourcentageTexte(donnees.tauxConformite),
        icone: ICONES.qualite,
      },
    ];
  });

  readonly etapes = computed<EtapeAvecPerte[]>(() => {
    const donnees = this.stats();
    if (!donnees) {
      return [];
    }
    return donnees.parEtape.map((etape, index) => ({
      ...etape,
      perte:
        index === 0
          ? null
          : etape.quantiteProduite -
            donnees.parEtape[index - 1].quantiteProduite,
    }));
  });

  readonly repartitionStatuts = computed<RepartitionStatut[]>(() => {
    const donnees = this.stats();
    if (!donnees) {
      return [];
    }
    return donnees.parStatut.map((bucket) => ({
      statut: bucket.statut,
      libelle:
        LIBELLES_STATUT[bucket.statut as StatutCommande] ?? bucket.statut,
      badge: badgeStatut(bucket.statut as StatutCommande),
      nombre: bucket.nombre,
    }));
  });

  readonly repartitionPriorites = computed<RepartitionPriorite[]>(() => {
    const donnees = this.stats();
    if (!donnees) {
      return [];
    }
    return donnees.parPriorite.map((bucket) => ({
      priorite: bucket.priorite,
      libelle:
        LIBELLES_PRIORITE[bucket.priorite as PrioriteCommande] ??
        bucket.priorite,
      badge: badgePriorite(bucket.priorite as PrioriteCommande),
      nombre: bucket.nombre,
    }));
  });

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement.set(true);
    this.erreur.set(null);

    this.statsService
      .production()
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
              : 'Impossible de charger les statistiques. Vérifiez que le serveur est démarré.',
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

  /** Largeur d'une jauge de rendement, bornée entre 0 et 100. */
  largeurRendement(valeur: number): number {
    return Math.min(100, Math.max(0, Math.round(valeur)));
  }

  formatNombre(valeur: number): string {
    return this.nombre(valeur);
  }

  formatMontant(valeur: number): string {
    return `${this.nombre(valeur)} DT`;
  }

  formatPourcentage(valeur: number): string {
    return this.pourcentageTexte(valeur);
  }

  formatJours(valeur: number): string {
    return `${this.nombre(valeur)} j`;
  }

  /** Classe de couleur d'une perte de production. */
  classePerte(perte: number | null): string {
    if (perte === null || perte === 0) {
      return '';
    }
    return perte > 0 ? 'text-danger' : 'text-success';
  }

  private nombre(valeur: number): string {
    return new Intl.NumberFormat('fr-FR', {
      maximumFractionDigits: 2,
    }).format(valeur);
  }

  private pourcentageTexte(valeur: number): string {
    return `${this.nombre(valeur)} %`;
  }
}
