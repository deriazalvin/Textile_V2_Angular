package com.example.textile.service.impl;

import com.example.textile.dto.response.ProductionStatisticsResponse;
import com.example.textile.entity.Commande;
import com.example.textile.entity.EtapeProduction;
import com.example.textile.entity.LigneCommande;
import com.example.textile.entity.PrioriteCommande;
import com.example.textile.entity.ResultatControle;
import com.example.textile.entity.StatutCommande;
import com.example.textile.entity.TypeEtape;
import com.example.textile.repository.CommandeRepository;
import com.example.textile.repository.ControleQualiteRepository;
import com.example.textile.repository.StatistiquesRepository;
import com.example.textile.service.StatistiquesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Statistiques de production : volumétrie, rendements, délais et qualité.
 * Toutes les lectures sont regroupées dans une seule transaction.
 */
@Service
@Transactional(readOnly = true)
public class StatistiquesServiceImpl implements StatistiquesService {

    private final StatistiquesRepository statistiquesRepository;
    private final CommandeRepository commandeRepository;
    private final ControleQualiteRepository controleQualiteRepository;

    public StatistiquesServiceImpl(StatistiquesRepository statistiquesRepository,
                                   CommandeRepository commandeRepository,
                                   ControleQualiteRepository controleQualiteRepository) {
        this.statistiquesRepository = statistiquesRepository;
        this.commandeRepository = commandeRepository;
        this.controleQualiteRepository = controleQualiteRepository;
    }

    @Override
    public ProductionStatisticsResponse statistiques() {
        List<Commande> delivered = commandeRepository.findDelivered();

        // ---------------------------------------------------------- volumétrie
        long totalCommandes = commandeRepository.count();
        long commandesLivrees = delivered.size();
        long commandesEnCours = commandeRepository.countByStatut(StatutCommande.EN_PRODUCTION)
                + commandeRepository.countByStatut(StatutCommande.EN_CONTROLE)
                + commandeRepository.countByStatut(StatutCommande.PLANIFIEE)
                + commandeRepository.countByStatut(StatutCommande.BROUILLON);
        long commandesAnnulees = commandeRepository.countByStatut(StatutCommande.ANNULEE);

        long quantiteCommandee = statistiquesRepository.quantiteCommandeeTotale();
        long quantiteProduite = statistiquesRepository.quantiteProduiteTotale();
        double rendementGlobal = ratio(quantiteProduite, quantiteCommandee);

        // ------------------------------------------------------------- délais
        long enRetard = commandeRepository.countByDateLivraisonPrevueBeforeAndStatutNotIn(
                LocalDate.now(), List.of(StatutCommande.LIVREE, StatutCommande.ANNULEE));
        long aTemps = statistiquesRepository.countDeliveredOnTime();
        long enRetardLivre = statistiquesRepository.countDeliveredLate();
        long totalLivre = aTemps + enRetardLivre;

        double delaiMoyen = delivered.isEmpty() ? 0
                : round(delivered.stream().mapToLong(Commande::getRetardJours).average().orElse(0));
        double tauxATemps = ratio(totalLivre, aTemps);

        // ------------------------------------------------------------ qualité
        long conformes = statistiquesRepository.countControlesParResultat(ResultatControle.CONFORME);
        long nonConformes = statistiquesRepository.countControlesParResultat(ResultatControle.NON_CONFORME);
        double tauxConformite = ratio(conformes + nonConformes, conformes);

        // --------------------------------------------------------- ventilations
        List<ProductionStatisticsResponse.StatutBucket> parStatut = new ArrayList<>();
        for (StatutCommande statut : StatutCommande.values()) {
            parStatut.add(new ProductionStatisticsResponse.StatutBucket(
                    statut.name(), commandeRepository.countByStatut(statut)));
        }

        List<ProductionStatisticsResponse.PrioriteBucket> parPriorite = new ArrayList<>();
        for (PrioriteCommande priorite : PrioriteCommande.values()) {
            parPriorite.add(new ProductionStatisticsResponse.PrioriteBucket(
                    priorite.name(), commandeRepository.countByPriorite(priorite)));
        }

        // ------------------------------------------------- rendement par étape
        Map<TypeEtape, long[]> etapeStats = new LinkedHashMap<>();
        for (Object[] row : statistiquesRepository.etapesParType(
                com.example.textile.entity.StatutEtape.TERMINEE)) {
            etapeStats.put((TypeEtape) row[0], new long[]{(long) row[1], ((Number) row[2]).longValue()});
        }
        List<ProductionStatisticsResponse.EtapeStat> parEtape = new ArrayList<>();
        for (TypeEtape type : TypeEtape.values()) {
            long[] valeurs = etapeStats.getOrDefault(type, new long[]{0, 0});
            parEtape.add(new ProductionStatisticsResponse.EtapeStat(
                    type.getLibelle(), type.getRang(), valeurs[0], valeurs[1],
                    ratio(valeurs[0], valeurs[0])));
        }

        // ------------------------------------------------ rendement par produit
        List<ProductionStatisticsResponse.ProduitRendement> parProduit = new ArrayList<>();
        for (Object[] row : statistiquesRepository.quantitesParProduit()) {
            long commande = ((Number) row[2]).longValue();
            long produit = ((Number) row[3]).longValue();
            parProduit.add(new ProductionStatisticsResponse.ProduitRendement(
                    (String) row[0], (String) row[1], commande, produit,
                    commande - produit, ratio(commande, produit)));
        }

        // ------------------------------------------------------- par client
        List<ProductionStatisticsResponse.ClientVolume> parClient = new ArrayList<>();
        for (Object[] row : statistiquesRepository.volumeParClient()) {
            parClient.add(new ProductionStatisticsResponse.ClientVolume(
                    (String) row[0], ((Number) row[1]).longValue(),
                    (BigDecimal) row[2]));
        }

        return new ProductionStatisticsResponse(
                totalCommandes, commandesLivrees, commandesEnCours, commandesAnnulees,
                quantiteCommandee, quantiteProduite, rendementGlobal,
                delaiMoyen, enRetardLivre, aTemps, tauxATemps, enRetard,
                conformes, nonConformes, tauxConformite,
                parStatut, parPriorite, parEtape, parProduit, parClient);
    }

    private static double ratio(long numerateur, long denominateur) {
        if (denominateur <= 0) {
            return 0;
        }
        return Math.round(numerateur * 10000.0 / denominateur) / 100.0;
    }

    private static double round(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
}