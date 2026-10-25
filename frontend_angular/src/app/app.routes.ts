import { Routes } from '@angular/router';
import { Utilisateurs } from './admin/utilisateurs/utilisateurs';
import { Liste as ClientsListe } from './clients/liste/liste';
import { Formulaire as ClientsFormulaire } from './clients/formulaire/formulaire';
import { CloturerEtape } from './commandes/cloturer-etape/cloturer-etape';
import { Detail } from './commandes/detail/detail';
import { Formulaire as CommandesFormulaire } from './commandes/formulaire/formulaire';
import { Liste as CommandesListe } from './commandes/liste/liste';
import { Livraison } from './commandes/livraison/livraison';
import { Production } from './commandes/production/production';
import { Qualite } from './commandes/qualite/qualite';
import { Retard } from './commandes/retard/retard';
import { AuthGuard, RoleGuard } from './core/guards/auth.guard';
import { Dashboard } from './dashboard/dashboard';
import { Erreur } from './erreur/erreur';
import { Inscription } from './inscription/inscription';
import { Barre_navigation } from './layout/barre_navigation/barre_navigation';
import { Login } from './login/login';
import { Liste as ProduitsListe } from './produits/liste/liste';
import { Formulaire as ProduitsFormulaire } from './produits/formulaire/formulaire';
import { Statistisques } from './statistisques/statistisques';

/**
 * Routes de l'application.
 *
 * <p>`login` et `inscription` sont hors de la coquille ; toutes les autres
 * pages sont protégées par `AuthGuard` et s'affichent dans
 * `Barre_navigation` (sidebar + topbar + router-outlet).</p>
 */
export const routes: Routes = [
  {
    path: 'login',
    title: 'Connexion · Textile',
    component: Login,
  },
  {
    path: 'inscription',
    title: 'Inscription · Textile',
    component: Inscription,
  },
  {
    path: '',
    component: Barre_navigation,
    canActivate: [AuthGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        title: 'Tableau de bord · Textile',
        data: { titre: 'Tableau de bord' },
        component: Dashboard,
      },

      // ------------------------------------------------------------ Commandes
      {
        path: 'commandes',
        title: 'Commandes · Textile',
        data: { titre: 'Commandes' },
        component: CommandesListe,
      },
      {
        path: 'commandes/nouvelle',
        title: 'Nouvelle commande · Textile',
        data: { titre: 'Nouvelle commande', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
        canActivate: [RoleGuard],
        component: CommandesFormulaire,
      },
      {
        path: 'commandes/:id/modifier',
        title: 'Modifier la commande · Textile',
        data: { titre: 'Modifier la commande', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
        canActivate: [RoleGuard],
        component: CommandesFormulaire,
      },
      {
        path: 'commandes/:id/production',
        title: 'Production · Textile',
        data: { titre: 'Production' },
        component: Production,
      },
      {
        path: 'commandes/:id/qualite',
        title: 'Contrôle qualité · Textile',
        data: { titre: 'Contrôle qualité' },
        component: Qualite,
      },
      {
        path: 'commandes/:id/livraison',
        title: 'Livraison · Textile',
        data: { titre: 'Livraison' },
        component: Livraison,
      },
      {
        path: 'commandes/:id/etapes/:etape/cloturer',
        title: 'Clôturer une étape · Textile',
        data: { titre: 'Clôturer une étape' },
        component: CloturerEtape,
      },
      {
        path: 'commandes/:id',
        title: 'Détail de la commande · Textile',
        data: { titre: 'Détail de la commande' },
        component: Detail,
      },
      {
        path: 'retards',
        title: 'Commandes en retard · Textile',
        data: { titre: 'Commandes en retard' },
        component: Retard,
      },

      // ---------------------------------------------------------------- Clients
      {
        path: 'clients',
        title: 'Clients · Textile',
        data: { titre: 'Clients', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
        canActivate: [RoleGuard],
        component: ClientsListe,
      },
      {
        path: 'clients/nouveau',
        title: 'Nouveau client · Textile',
        data: { titre: 'Nouveau client', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
        canActivate: [RoleGuard],
        component: ClientsFormulaire,
      },
      {
        path: 'clients/:id/modifier',
        title: 'Modifier le client · Textile',
        data: { titre: 'Modifier le client', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
        canActivate: [RoleGuard],
        component: ClientsFormulaire,
      },

      // --------------------------------------------------------------- Produits
      {
        path: 'produits',
        title: 'Produits · Textile',
        data: { titre: 'Produits', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
        canActivate: [RoleGuard],
        component: ProduitsListe,
      },
      {
        path: 'produits/nouveau',
        title: 'Nouveau produit · Textile',
        data: { titre: 'Nouveau produit', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
        canActivate: [RoleGuard],
        component: ProduitsFormulaire,
      },
      {
        path: 'produits/:id/modifier',
        title: 'Modifier le produit · Textile',
        data: { titre: 'Modifier le produit', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] },
        canActivate: [RoleGuard],
        component: ProduitsFormulaire,
      },

      // ---------------------------------------------------------- Statistiques
      {
        path: 'statistiques',
        title: 'Statistiques · Textile',
        data: { titre: 'Statistiques' },
        component: Statistisques,
      },

      // -------------------------------------------------------- Administration
      {
        path: 'admin/utilisateurs',
        title: 'Utilisateurs · Textile',
        data: { titre: 'Utilisateurs', roles: ['ROLE_ADMIN'] },
        canActivate: [RoleGuard],
        component: Utilisateurs,
      },

      // ------------------------------------------------------------------ 404
      {
        path: '**',
        title: 'Page introuvable · Textile',
        component: Erreur,
      },
    ],
  },
];
