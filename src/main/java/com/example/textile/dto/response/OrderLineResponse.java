package com.example.textile.dto.response;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long id,
        Long produitId,
        String produitCode,
        String produitDesignation,
        Integer quantite,
        String taille,
        String couleur,
        BigDecimal prixUnitaire,
        BigDecimal montant,
        Integer quantiteProduite,
        Integer quantiteRestante) {
}