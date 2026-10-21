package com.example.textile.entity;

/**
 * Resultat du controle qualite obligatoire avant livraison.
 */
public enum ResultatControle {
    CONFORME,
    NON_CONFORME;

    /** Classe CSS de la pastille de resultat. */
    public String getBadgeClass() {
        return this == CONFORME ? "badge--success" : "badge--danger";
    }

    /** Classe CSS de l'encart d'alerte. */
    public String getAlertClass() {
        return this == CONFORME ? "alert--success" : "alert--error";
    }
}
