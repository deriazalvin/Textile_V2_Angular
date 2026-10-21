package com.example.textile.entity;

/**
 * Statut d'une etape de production.
 */
public enum StatutEtape {
    NON_COMMENCEE,
    EN_COURS,
    TERMINEE,
    BLOQUEE;

    /** Classe CSS du badge de statut. */
    public String getBadgeClass() {
        return switch (this) {
            case TERMINEE -> "badge--success";
            case EN_COURS -> "badge--warning";
            case BLOQUEE -> "badge--danger";
            default -> "badge--muted";
        };
    }

    /** Classe CSS de la carte d'etape dans le stepper. */
    public String getStepperClass() {
        return switch (this) {
            case TERMINEE -> "is-done";
            case EN_COURS -> "is-current";
            default -> "is-todo";
        };
    }
}
