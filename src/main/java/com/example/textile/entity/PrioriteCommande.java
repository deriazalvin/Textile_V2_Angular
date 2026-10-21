package com.example.textile.entity;

/**
 * Priorite d'une commande. Les commandes urgentes remontent en tete du dashboard.
 */
public enum PrioriteCommande {
    NORMALE(0),
    URGENTE(1),
    TRES_URGENTE(2);

    private final int poids;

    PrioriteCommande(int poids) {
        this.poids = poids;
    }

    public int getPoids() {
        return poids;
    }

    /** Classe CSS du badge associe a la priorite. */
    public String getBadgeClass() {
        return switch (this) {
            case TRES_URGENTE -> "badge--danger";
            case URGENTE -> "badge--warning";
            default -> "badge--muted";
        };
    }
}