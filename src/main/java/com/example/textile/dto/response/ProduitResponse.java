package com.example.textile.dto.response;

import java.math.BigDecimal;

public record ProduitResponse(
        Long id,
        String code,
        String designation,
        String categorie,
        String matiere,
        BigDecimal prixUnitaire,
        Integer stock,
        boolean active) {

    /** Classe CSS du badge actif / inactif. */
    public String activeBadgeClass() {
        return active ? "badge--success" : "badge--muted";
    }

    /** Libelle du badge actif / inactif. */
    public String activeLabel() {
        return active ? "Actif" : "Inactif";
    }
}