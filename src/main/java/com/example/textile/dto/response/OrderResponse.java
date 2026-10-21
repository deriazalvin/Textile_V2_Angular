package com.example.textile.dto.response;

import com.example.textile.entity.PrioriteCommande;
import com.example.textile.entity.StatutCommande;
import com.example.textile.entity.TypeEtape;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrderResponse(
        Long id,
        String numero,
        Long clientId,
        String clientNom,
        String clientTelephone,
        LocalDate dateCommande,
        LocalDate dateLivraisonPrevue,
        LocalDate dateLivraisonReelle,
        PrioriteCommande priorite,
        StatutCommande statut,
        String commentaire,
        String instructionsSpeciales,
        BigDecimal montantTotal,
        Integer quantiteTotale,
        Integer quantiteProduiteTotale,
        Long dureePrevueJours,
        Long dureeReelleJours,
        Long retardJours,
        boolean enRetard,
        TypeEtape etapeCourante,
        List<OrderLineResponse> lignes,
        List<StepResponse> etapes,
        QualityControlResponse controleQualite) {
}