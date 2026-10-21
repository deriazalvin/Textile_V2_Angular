package com.example.textile.entity;

/**
 * Statuts possibles d'une commande.
 */
public enum StatutCommande {
    /** Commande enregistree, production non encore demarree. */
    BROUILLON,
    /** Commande validee, en attente de lancement de la production. */
    PLANIFIEE,
    /** Production en cours (au moins une etape demarree). */
    EN_PRODUCTION,
    /** Controle qualite en attente. */
    EN_CONTROLE,
    /** Commande livree. Terminal. */
    LIVREE,
    /** Commande annulee. Terminal. */
    ANNULEE;

    /** Classe CSS du badge associe au statut (voir app.css). */
    public String getBadgeClass() {
        return switch (this) {
            case LIVREE -> "badge--success";
            case ANNULEE -> "badge--danger";
            case EN_PRODUCTION, EN_CONTROLE -> "badge--warning";
            default -> "badge--info";
        };
    }
}