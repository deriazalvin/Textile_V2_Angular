package com.example.textile.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Statistiques de production :eted view agregée pour le tableau de bord
 * de pilotage et la page « Statistiques ».
 */
public record ProductionStatisticsResponse(
        // Volumétrie
        long totalCommandes,
        long commandesLivrees,
        long commandesEnCours,
        long commandesAnnulees,
        long quantiteCommandee,
        long quantiteProduite,
        double rendementGlobal,

        // Délais
        double delaiMoyenJours,
        long commandesLivreesEnRetard,
        long commandesLivreesATemps,
        double tauxLivraisonATemps,
        long commandesEnRetard,

        // Qualité
        long controlesConformes,
        long controlesNonConformes,
        double tauxConformite,

        // Détails
        List<StatutBucket> parStatut,
        List<PrioriteBucket> parPriorite,
        List<EtapeStat> parEtape,
        List<ProduitRendement> parProduit,
        List<ClientVolume> parClient) {

    public record StatutBucket(String statut, long nombre) {}

    public record PrioriteBucket(String priorite, long nombre) {}

    /** Statistiques d'une étape du workflow. */
    public record EtapeStat(
            String etape,
            int rang,
            long etapesTerminees,
            long quantiteProduite,
            double tauxReussite) {}

    /** Rendement de production par produit (perte entre l'étape initiale et la finition). */
    public record ProduitRendement(
            String code,
            String designation,
            long quantiteCommandee,
            long quantiteProduite,
            long ecart,
            double rendement) {}

    public record ClientVolume(String client, long commandes, BigDecimal montant) {}
}