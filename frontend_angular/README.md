# Frontend Angular — Textile

Interface de pilotage des commandes textiles (Angular 22, **NgModule**, SPA).
Elle consomme l'API REST Spring Boot du dossier parent (`/api`).

## Démarrage

```bash
# 1. Backend (depuis la racine du dépôt)
./mvnw spring-boot:run

# 2. Frontend (dans ce dossier)
npm install
npm start          # http://localhost:4200
```

Le serveur de développement proxifie `/api` vers `http://localhost:8080`
(voir `proxy.conf.json`) : aucune configuration CORS n'est nécessaire.

### Comptes de démonstration

| Rôle | Identifiant | Mot de passe |
|---|---|---|
| ADMIN | `admin` | `admin123` |
| MANAGER | `manager` | `manager123` |
| OPERATOR | `operateur` | `operateur123` |
| USER | `consultation` | `user123` |

## Scripts

| Commande | Description |
|---|---|
| `npm start` | serveur de développement avec proxy API |
| `npm run build` | build de production |
| `npm test` | tests unitaires (Vitest) |

## Organisation

```text
src/app/
├── core/
│   ├── http/          helper HttpParams
│   ├── models/        types et enums miroir des DTO du backend
│   └── services/      accès REST (clients, produits, commandes, production, stats, users)
├── admin/             administration des comptes
├── clients/           liste, formulaire, carte client
├── commandes/         liste, formulaire, détail, workflow (production, qualité, livraison, retard)
├── dashboard/         tableau de bord
├── statistisques/     statistiques de production
├── layout/            coquille applicative (barre de navigation)
├── fragments/         composants réutilisables (pagination)
├── login/             connexion
└── inscription/       création de compte
```

### Architecture NgModule

L'application est découpée en modules métier. Chaque composant est déclaré
(`declarations`) dans le module de son domaine et n'est plus `standalone`.

```text
AppModule (src/app/app.module.ts)
├── AppRoutingModule          RouterModule.forRoot + routes
├── FragmentsModule           CommonModule, RouterModule, Pagination
├── AuthModule                Login, Inscription
├── LayoutModule              Barre_navigation
├── DashboardModule           Dashboard
├── ClientsModule             Liste, Formulaire, Carte
├── ProduitsModule            Liste, Formulaire
├── CommandesModule           Liste, Formulaire, Détail, Production, Qualité, Livraison…
├── StatistiquesModule        Statistisques
└── AdminModule               Utilisateurs
```

Le bootstrap se fait dans `src/main.ts` via
`platformBrowserDynamic().bootstrapModule(AppModule)`.
Les guards (`AuthGuard`, `RoleGuard`) et l'intercepteur (`AuthInterceptor`)
sont des classes injectables enregistrées dans `AppModule`.

Les styles globaux (`src/styles.scss`) reprennent le design system du backend
(tokens + composants) afin de conserver une identité visuelle unique.

## Authentification

- L'API Spring Boot est protégée par **HTTP Basic**.
- `AuthService` encode les identifiants et les conserve dans `sessionStorage`
  (effacés à la fermeture de l'onglet) ; `AuthInterceptor` ajoute l'en-tête
  `Authorization` à chaque appel `/api/**`.
- `GET /api/auth/me` renvoie l'identité et le rôle de l'utilisateur courant ;
  le menu latéral et les routes sont filtrés par rôle (`AuthGuard`, `RoleGuard`).
- `POST /api/auth/register` crée un compte de consultation (`ROLE_USER`).
  Les comptes ADMIN / MANAGER / OPERATOR sont créés par l'administrateur.

