package com.example.textile.dto.response;

import com.example.textile.entity.StatutCommande;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public record DashboardResponse(
        long totalCommandes,
        long commandesEnProduction,
        long commandesEnRetard,
        long commandesLivrees,
        long commandesUrgent,
        BigDecimal chiffreAffairesLivree,
        double tauxConformite,
        double delaiMoyenLivraisonJours,
        Map<StatutCommande, Long> repartitionParStatut,
        List<OrderResponse> commandesPrioritaires,
        List<OrderResponse> commandesEnRetardListe) {
}