import { DOCUMENT } from '@angular/common';
import { Component, OnDestroy, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Subscription, filter } from 'rxjs';
import { AppRole, LIBELLES_ROLE } from '../../core/models/enums';
import { AuthService } from '../../core/services/auth.service';

interface LienNavigation {
  chemin: string;
  libelle: string;
  icone: string;
  roles?: AppRole[];
}

interface GroupeNavigation {
  libelle: string;
  liens: LienNavigation[];
}

/** Icônes SVG (attribut `d`) utilisées dans la barre latérale. */
const ICONES = {
  dashboard: 'M3 12l9-9 9 9M5 10v10h5v-6h4v6h5V10',
  commandes:
    'M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2M9 5h6M9 12h6M9 16h4',
  retard: 'M12 8v4l3 2M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18z',
  clients:
    'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75',
  produits:
    'M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16zM3.27 6.96 12 12.01l8.73-5.05M12 22.08V12',
  stats: 'M3 3v18h18M7 15v3M12 9v9M17 5v13',
  admin:
    'M12 15a3 3 0 1 0 0-6 3 3 0 0 0 0 6zM19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 1 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 1 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 1 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 1 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z',
  deconnexion: 'M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9',
  menu: 'M3 6h18M3 12h18M3 18h18',
};

const GROUPES: GroupeNavigation[] = [
  {
    libelle: 'Pilotage',
    liens: [
      { chemin: '/dashboard', libelle: 'Tableau de bord', icone: ICONES.dashboard },
      { chemin: '/commandes', libelle: 'Commandes', icone: ICONES.commandes },
      { chemin: '/retards', libelle: 'Retards', icone: ICONES.retard },
      { chemin: '/statistiques', libelle: 'Statistiques', icone: ICONES.stats },
    ],
  },
  {
    libelle: 'Référentiels',
    liens: [
      {
        chemin: '/clients',
        libelle: 'Clients',
        icone: ICONES.clients,
        roles: ['ROLE_ADMIN', 'ROLE_MANAGER'],
      },
      {
        chemin: '/produits',
        libelle: 'Produits',
        icone: ICONES.produits,
        roles: ['ROLE_ADMIN', 'ROLE_MANAGER'],
      },
    ],
  },
  {
    libelle: 'Administration',
    liens: [
      {
        chemin: '/admin/utilisateurs',
        libelle: 'Utilisateurs',
        icone: ICONES.admin,
        roles: ['ROLE_ADMIN'],
      },
    ],
  },
];

/**
 * Coquille applicative : barre latérale, barre supérieure et contenu routé.
 * Les entrées de menu sont filtrées selon le rôle de l'utilisateur connecté.
 */
@Component({
  standalone: false,
  selector: 'app-barre_navigation',
  styleUrl: './barre_navigation.scss',
  templateUrl: './barre_navigation.html',
})
export class Barre_navigation implements OnDestroy {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly document = inject(DOCUMENT);

  private readonly abonnements = new Subscription();

  readonly utilisateur = this.auth.utilisateur;
  readonly role = this.auth.role;
  readonly menuOuvert = signal(false);

  titrePage = 'Tableau de bord';

  readonly groupes = computed<GroupeNavigation[]>(() => {
    const role = this.role();
    return GROUPES.map((groupe) => ({
      ...groupe,
      liens: groupe.liens.filter(
        (lien) => !lien.roles || (role !== null && lien.roles.includes(role)),
      ),
    })).filter((groupe) => groupe.liens.length > 0);
  });

  readonly initiales = computed(() => {
    const utilisateur = this.utilisateur();
    const base = utilisateur?.displayName || utilisateur?.username || '';
    return base
      .split(/\s+/)
      .filter((mot) => mot.length > 0)
      .slice(0, 2)
      .map((mot) => mot.charAt(0))
      .join('')
      .toUpperCase();
  });

  readonly libelleRole = computed(() => {
    const role = this.role();
    return role === null ? '' : LIBELLES_ROLE[role];
  });

  constructor() {
    this.titrePage = this.titreDepuisUrl(this.router.url);
    this.abonnements.add(
      this.router.events
        .pipe(filter((evenement) => evenement instanceof NavigationEnd))
        .subscribe(() => {
          this.titrePage = this.titreDepuisUrl(this.router.url);
          this.fermerMenu();
        }),
    );
  }

  ngOnDestroy(): void {
    this.abonnements.unsubscribe();
    this.document.body.removeAttribute('data-drawer');
  }

  basculerMenu(): void {
    this.menuOuvert.update((ouvert) => !ouvert);
    this.majDrawer();
  }

  fermerMenu(): void {
    if (!this.menuOuvert()) {
      return;
    }
    this.menuOuvert.set(false);
    this.majDrawer();
  }

  deconnecter(): void {
    this.auth.deconnecter();
  }

  private majDrawer(): void {
    this.document.body.setAttribute(
      'data-drawer',
      this.menuOuvert() ? 'open' : 'closed',
    );
  }

  /** Titre affiché dans la barre supérieure, déduit de l'URL courante. */
  private titreDepuisUrl(url: string): string {
    const chemin = url.split('?')[0].split('#')[0];
    const liens = GROUPES.flatMap((groupe) => groupe.liens);
    const lien = liens.find(
      (candidat) =>
        chemin === candidat.chemin || chemin.startsWith(`${candidat.chemin}/`),
    );
    if (lien) {
      return lien.libelle;
    }
    if (chemin.startsWith('/commandes/nouvelle')) {
      return 'Nouvelle commande';
    }
    if (/^\/commandes\/\d+\/production/.test(chemin)) {
      return 'Production';
    }
    if (/^\/commandes\/\d+\/qualite/.test(chemin)) {
      return 'Contrôle qualité';
    }
    if (/^\/commandes\/\d+\/livraison/.test(chemin)) {
      return 'Livraison';
    }
    if (/^\/commandes\/\d+\/etapes/.test(chemin)) {
      return "Clôture d'étape";
    }
    if (/^\/commandes\/\d+\/modifier/.test(chemin)) {
      return 'Modifier la commande';
    }
    if (/^\/commandes\/\d+/.test(chemin)) {
      return 'Détail de la commande';
    }
    if (chemin.startsWith('/clients/nouveau')) {
      return 'Nouveau client';
    }
    if (/^\/clients\/\d+\/modifier/.test(chemin)) {
      return 'Modifier le client';
    }
    if (chemin.startsWith('/produits/nouveau')) {
      return 'Nouveau produit';
    }
    if (/^\/produits\/\d+\/modifier/.test(chemin)) {
      return 'Modifier le produit';
    }
    return 'Textile';
  }
}
