package com.example.textile.dto.response;

import com.example.textile.entity.TypeEtape;

import java.time.LocalDate;
import java.util.List;

/**
 * Vue "production" d'une commande : etapes, quantites, avancement, retard.
 */
public record ProductionResponse(
        Long commandeId,
        String numero,
        TypeEtape etapeCourante,
        int etapeCouranteRang,
        int etapesTerminees,
        int etapesTotal,
        int avancementPourcentage,
        Integer quantiteCommandee,
        Integer quantiteProduite,
        LocalDate dateLivraisonPrevue,
        Long retardJours,
        boolean enRetard,
        List<OrderLineResponse> lignes,
        List<StepResponse> etapes,
        QualityControlResponse controleQualite) {
}