# Projet 2 — Système de gestion de commandes dans une entreprise textile

Suivi d'une commande textile depuis sa création jusqu'à sa livraison, avec un
workflow de production strict et deux points d'accès partageant la même couche métier :

- une **API REST** (JSON, HTTP Basic, DTO, codes HTTP corrects) ;
- une **application MVC Thymeleaf** (formulaire de login, vues serveur).

---

## 1. Démarrage

```bash
./mvnw spring-boot:run
```

- Application : <http://localhost:8080>
- Base par défaut : **H2 en mémoire**, aucune installation requise.

### Configuration de la base de données

Les identifiants de la base **ne sont jamais versionnés**. Toute la configuration
sensible vit dans `src/main/resources/application-local.properties`, fichier
ignoré par Git (voir `.gitignore`) et activé par le profil `local`.

Pour travailler sur **MariaDB / MySQL** en local :

```bash
cd src/main/resources
cp application-local.properties.example application-local.properties
# puis renseignez vos identifiants dans application-local.properties
```

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/textile?useSSL=false&serverTimezone=UTC
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=<votre mot de passe>
spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect
```

Sans ce fichier, l'application démarre automatiquement sur H2 en mémoire.

> ⚠️ Ne placez jamais un mot de passe en clair dans
> `src/main/resources/application.properties` : ce fichier est versionné.

### Comptes de démonstration

| Rôle | Identifiant | Mot de passe | Droits |
|---|---|---|---|
| ADMIN | `admin` | `admin123` | configuration + administration des comptes |
| MANAGER | `manager` | `manager123` | gestion métier (clients, produits, commandes) |
| OPERATOR | `operateur` | `operateur123` | opérations quotidiennes (production, qualité, livraison) |
| USER | `consultation` | `user123` | consultation seule |

---

## 2. Base de données

H2 **en mémoire**, schéma généré par JPA (`ddl-auto=update`) : la base est
reconstruite et repeuplée à chaque démarrage (désactivable avec
`textile.demo-data=false`).

### Entités

| Table | Rôle |
|---|---|
| `clients` | référentiel clients |
| `produits` | catalogue textile (code, désignation, matière, prix) |
| `commandes` | commande : numéro, client, dates, priorité, statut, montant |
| `lignes_commande` | produit, quantité, taille, couleur, prix unitaire, **quantité produite** |
| `etapes_production` | une ligne par étape du workflow, avec statut, responsable, dates, quantité produite |
| `controles_qualite` | résultat CONFORME / NON_CONFORME (1 par commande) |
| `livraisons` | preuve de livraison (réceptionnaire, bon, date) |
| `app_users` | comptes et rôles |

L'unicité `(commande_id, type_etape)` sur `etapes_production` garantit qu'une
étape ne peut pas être dupliquée.

---

## 3. Architecture

```text
com.example.textile
├── config        SecurityConfig, DataInitializer, WebMvcConfig
├── controller
│   ├── rest      CommandeRestController, ProductionRestController,
│   │             ClientRestController, ProduitRestController,
│   │             StatsRestController, UserRestController
│   └── web       DashboardController, CommandeController, ProductionController,
│                 ClientController, ProduitController, AdminController, LoginController
├── dto
│   ├── request   Create/Update…Request, OrderForm, StepCompletionForm
│   └── response  OrderResponse, ProductionResponse, PageResponse, ErrorResponse…
├── entity        Commande, LigneCommande, EtapeProduction, ControleQualite,
│                 Livraison, Client, Produit, AppUser + énumérations
├── repository    Spring Data JPA, requêtes JPQL de recherche/filtrage
├── service
│   └── impl      CommandeServiceImpl, ProductionServiceImpl, ClientServiceImpl,
│                 ProduitServiceImpl, DashboardServiceImpl
├── exception     ResourceNotFound, Business, InvalidOperation, Conflict
│                 + GlobalExceptionHandler (REST) / WebExceptionHandler (Thymeleaf)
├── mapper        ClientMapper, ProduitMapper, CommandeMapper
├── security      AppRole, UserDetailsService, handlers 401/403 JSON
└── util          PageableUtils, OrderNumberGenerator, CurrentUser
```

Aucune entité JPA n'est exposée par l'API REST : tout passe par des records DTO.

---

## 4. Workflow de production

```text
COMMANDE → COUPE → COUTURE → FINITION → CONTROLE_QUALITE → LIVRAISON
```

`POST /api/orders/{id}/production/start` crée les 6 étapes, clôture `COMMANDE`
(les quantités commandées deviennent la base de production) et démarre `COUPE`.

### Règles métier appliquées

| Règle | Erreur retournée |
|---|---|
| Lancer deux fois la production | `ConflictException` → 409 |
| Démarrer/terminer une étape sans la précédente terminée | `InvalidOperationException` → 400 |
| Produire plus que la quantité commandée | `BusinessException` → 400 |
| Produire plus que la quantité de l'étape précédente | `BusinessException` → 400 |
| Contrôle qualité avant la fin de la finition | `InvalidOperationException` → 400 |
| Livrer une commande non contrôlée | `BusinessException` → 400 |
| Livrer une commande NON CONFORME | `InvalidOperationException` → 400 |
| Modifier / supprimer une commande livrée | `InvalidOperationException` → 400 |

Toutes ces opérations sont `@Transactional`.

### Exemple de référence (issu des tests)

Commande de **500 chemises** → coupe **500** → couture **480** → finition **450**.
Déclarer 500 à la finition est refusé : l'étape précédente n'a produit que 480.

---

## 5. Retards et priorités

- `dureePrevueJours`, `dureeReelleJours`, `retardJours` sont calculés sur la commande.
- `GET /api/orders/delayed` et `/commandes/retard` listent les commandes non livrées
  dont la date prévue est dépassée.
- Priorités `NORMALE`, `URGENTE`, `TRES_URGENTE` ; le tableau de bord remonte
  d'abord les commandes urgentes puis les plus proches de l'échéance.

---

## 6. API REST

Authentification HTTP Basic sur `/api/**` (réponses 401/403 en JSON).

```http
GET    /api/clients?page=0&size=10&sort=name,asc&search=mode&active=true
POST   /api/clients
PUT    /api/clients/{id}
DELETE /api/clients/{id}

GET    /api/produits?search=chemise&categorie=Chemises&page=0&size=10
POST   /api/produits

GET    /api/orders?page=0&size=10&sort=dateCommande,desc&statut=EN_PRODUCTION&priorite=URGENTE
GET    /api/orders/delayed
GET    /api/orders/active
GET    /api/orders/{id}
POST   /api/orders                                → 201 Created
PUT    /api/orders/{id}
DELETE /api/orders/{id}
POST   /api/orders/{id}/cancel

POST   /api/orders/{id}/production/start
POST   /api/orders/{id}/production/steps/{step}/start
POST   /api/orders/{id}/production/steps/{step}/complete
GET    /api/orders/{id}/production
POST   /api/orders/{id}/quality-control
POST   /api/orders/{id}/delivery

GET    /api/stats/dashboard
GET    /api/stats/production           ← statistiques de production (§12)
GET    /api/users                (ADMIN)
```

Les 9 endpoints listés dans l'énoncé sont tous implémentés. En supplément :
`POST /api/orders/{id}/production/steps/{step}/start`, `POST /api/orders/{id}/cancel`,
`GET /api/orders/active`, `GET /api/orders/numero/{numero}` et le référentiel
`GET/POST/PUT/DELETE /api/clients` et `/api/produits`.

### Exemple — création puis workflow complet

```bash
# 1. référentiel
curl -u manager:manager123 -X POST localhost:8080/api/clients \
  -H 'Content-Type: application/json' \
  -d '{"name":"Textil Est","phone":"+216 71 100 200","city":"Sousse"}'

# 2. commande (201 Created)
curl -u manager:manager123 -X POST localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -d '{"clientId":1,"dateLivraisonPrevue":"2026-12-15","priorite":"URGENTE",
       "lignes":[{"produitId":1,"quantite":500,"taille":"M","couleur":"Blanc"}]}'

# 3. workflow
curl -u operateur:operateur123 -X POST localhost:8080/api/orders/1/production/start
curl -u operateur:operateur123 -X POST localhost:8080/api/orders/1/production/steps/COUPE/complete \
  -H 'Content-Type: application/json' -d '{"quantites":[{"ligneId":1,"quantite":500}]}'
curl -u operateur:operateur123 -X POST localhost:8080/api/orders/1/production/steps/COUTURE/complete \
  -H 'Content-Type: application/json' -d '{"quantites":[{"ligneId":1,"quantite":480}]}'
curl -u operateur:operateur123 -X POST localhost:8080/api/orders/1/production/steps/FINITION/complete \
  -H 'Content-Type: application/json' -d '{"quantites":[{"ligneId":1,"quantite":450}]}'

# 4. contrôle qualité puis livraison
curl -u operateur:operateur123 -X POST localhost:8080/api/orders/1/quality-control \
  -H 'Content-Type: application/json' -d '{"resultat":"CONFORME"}'
curl -u operateur:operateur123 -X POST localhost:8080/api/orders/1/delivery \
  -H 'Content-Type: application/json' -d '{"receptionnaire":"M. Ben Salah","bonLivraison":"BL-001"}'
```

### Pagination, tri, recherche, filtres

`PageResponse<T>` expose `content, page, size, totalElements, totalPages, first, last, sort`.
Les champs de tri sont sur une **liste blanche** (`PageableUtils`) et la taille de
page est plafonnée à 200.

---

## 7. Erreurs

`ErrorResponse` uniforme :

```json
{
  "timestamp": "2026-10-08T16:29:12.094917",
  "status": 400,
  "error": "Bad Request",
  "message": "Donnees invalides : La date prevue de livraison ne peut pas etre dans le passe",
  "path": "/api/orders",
  "fieldErrors": { "dateLivraisonPrevue": "..." }
}
```

L'interface Thymeleaf affiche les mêmes messages via `WebExceptionHandler`
(redirection vers une page d'erreur lisible) et des messages flash après écriture.

---

## 8. Tests

```bash
./mvnw test
```

`WorkflowMetierTest` couvre 10 scénarios : création, démarrage de production,
non-reproductibilité, ordre des étapes, scénario 500/480/450 complet, dépassement
de quantité, blocage de livraison sur NON CONFORME, contrôle qualité prématuré,
détection de priorité.

---

## 9. Interface — design system

L'interface suit une architecture de tokens à trois niveaux, sans framework CSS :

| Niveau | Fichier | Rôle |
|---|---|---|
| 1. Primitives | `static/css/tokens.css` | rampes de couleurs, échelle d'espacement (base 4px), rayons, ombres, transitions |
| 2. Sémantique | idem | `--bg-surface`, `--fg-muted`, `--danger`, `--success`… (rôles, jamais de couleur brute) |
| 3. Composants | `static/css/app.css` | boutons, champs, badges, tableaux, KPI, stepper, toasts |

Aucun composant ne référence une primitive directement.

### Composants

- **Coquille applicative** : sidebar fixe (≥1024px), tiroir off-canvas + voile en dessous, topbar collante avec fil d'Ariane et menu utilisateur.
- **KPI** : cartes à liseré coloré par criticité, icône, animation d'entrée décalée, valeur en chiffres tabulaires.
- **Tableaux** : en-tête collant, en-têtes `scope="col"`, surbrillance au survol, ligne alerte en rouge, défilement horizontal, colonne numérique alignée à droite.
- **Stepper de workflow** : 6 étapes en grille (2 colonnes puis 1 en mobile), marqueur d'état terminé / en cours / à venir, jauge de progression globale.
- **Badges** de statut et de priorité, **jauges** de production, **états vides** illustrés, **toasts** `aria-live` avec fermeture et auto-masquage des succès.
- **Formulaires** : labels au-dessus, aide et erreur sous le champ, `aria-invalid` et `aria-describedby`, focus visible, lignes de commande ajoutables dynamiquement.

### Pièges Thymeleaf rencontrés

Deux pièges ont caused des pages **tronquées sans erreur visible** ; ils sont documentés pour éviter toute régression.

1. **`th:classappend` n'accepte pas un ternaire** dans cette version de Thymeleaf : le processeur tente de ré-analyser la valeur et échoue (`Could not parse as expression`). La classe est donc calculée **en Java** sur les énumérations et les DTO :
   `StatutCommande.getBadgeClass()`, `PrioriteCommande.getBadgeClass()`,
   `StatutEtape.getStepperClass()`, `ResultatControle.getAlertClass()`,
   `ClientResponse.activeBadgeClass()`, `ProduitResponse.activeBadgeClass()`,
   `AppUser.getEnabledBadgeClass()`. Les templates font `th:classappend="${commande.statut.badgeClass}"`.
   Pour les variantes conditionnelles ponctuelles (jauge de rendement), on utilise deux
   éléments `th:if` / `th:unless`.

2. **Un statut HTTP 200 ne prouve pas qu'une page est rendue.** Quand une exception survient
   après l'écriture du tampon de réponse (8 Ko), Tomcat renvoie le début de la page avec un
   code 200 : la page est tronquée et silencieuse. Il faut donc contrôler
   (a) la présence de **toutes** les sections attendues dans le HTML,
   (b) le nombre d'erreurs `Exception processing template` dans les logs.

### Accessibilité (WCAG 2.1 AA)

- HTML sémantique : `<nav>`, `<main>`, `<table>` avec `scope`, `<fieldset>`/`<legend>`, `<label for>` partout.
- Lien d'évitement « Aller au contenu principal », `<main tabindex="-1">`.
- Anneau de focus visible (`:focus-visible`, 2px + décalage) sur tous les éléments interactifs.
- Contrastes : texte ≥ 4,5:1, composants et bordures ≥ 3:1 (rampes `slate-700+` sur fond clair, `indigo-700` pour le texte sur `indigo-50`).
- Notifications dans une région `role="status" aria-live="polite"`, erreurs en `role="alert"`.
- Jauges décoratives portent un `role="img"` avec `aria-label` décrivant la valeur.
- Icônes décoratives `aria-hidden="true"` ; boutons icône nommés via `aria-label`.
- Navigation courante signalée par `aria-current="page"`.
- `prefers-reduced-motion` : animations et transitions désactivées.

### Responsive

| Palier | Adaptations |
|---|---|
| ≥1024px | sidebar visible, contenu en grille confortable |
| 640–1023px | tiroir, KPI sur 2 colonnes, stepper sur 2 colonnes |
| <640px | KPI sur 1 colonne, stepper empilé, tableaux défilables, meta utilisateur masquée |

---

## 10. Choix techniques

- **H2 en mémoire** pour un démarrage immédiat sans installation ; le schéma est
  un artefact du développement, il suffit de remplacer les propriétés
  `spring.datasource.*` pour viser PostgreSQL/MySQL.
- **Spring Boot 4** / **Spring Framework 7** / **Java 21** ; Jackson 3 (`tools.jackson`).
- **Thymeleaf** en mode HTML (décoffage des pages) pour contrôler le HTML produit.
- Deux `SecurityFilterChain` : l'une Basic + JSON pour l'API, l'autre formulaire
  de login + CSRF pour les vues.