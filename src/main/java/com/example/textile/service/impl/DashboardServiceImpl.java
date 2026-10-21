package com.example.textile.service.impl;

import com.example.textile.dto.response.DashboardResponse;
import com.example.textile.dto.response.OrderResponse;
import com.example.textile.entity.Commande;
import com.example.textile.entity.PrioriteCommande;
import com.example.textile.entity.ResultatControle;
import com.example.textile.entity.StatutCommande;
import com.example.textile.mapper.CommandeMapper;
import com.example.textile.repository.CommandeRepository;
import com.example.textile.repository.ControleQualiteRepository;
import com.example.textile.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final CommandeRepository commandeRepository;
    private final ControleQualiteRepository controleQualiteRepository;
    private final CommandeMapper commandeMapper;

    public DashboardServiceImpl(CommandeRepository commandeRepository,
                                ControleQualiteRepository controleQualiteRepository,
                                CommandeMapper commandeMapper) {
        this.commandeRepository = commandeRepository;
        this.controleQualiteRepository = controleQualiteRepository;
        this.commandeMapper = commandeMapper;
    }

    @Override
    public DashboardResponse getDashboard() {
        long total = commandeRepository.count();
        long enProduction = commandeRepository.countByStatut(StatutCommande.EN_PRODUCTION)
                + commandeRepository.countByStatut(StatutCommande.EN_CONTROLE);
        long retards = commandeRepository.countByDateLivraisonPrevueBeforeAndStatutNotIn(
                LocalDate.now(), List.of(StatutCommande.LIVREE, StatutCommande.ANNULEE));
        long urgentes = commandeRepository.countByPriorite(PrioriteCommande.URGENTE)
                + commandeRepository.countByPriorite(PrioriteCommande.TRES_URGENTE);

        List<Commande> delivered = commandeRepository.findDelivered();

        Map<StatutCommande, Long> repartition = new EnumMap<>(StatutCommande.class);
        Arrays.stream(StatutCommande.values())
                .forEach(s -> repartition.put(s, commandeRepository.countByStatut(s)));

        BigDecimal chiffreAffaires = delivered.stream()
                .map(Commande::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long conformes = controleQualiteRepository.countByResultat(ResultatControle.CONFORME);
        long nonConformes = controleQualiteRepository.countByResultat(ResultatControle.NON_CONFORME);
        long totalControles = conformes + nonConformes;
        double tauxConformite = totalControles == 0 ? 100.0
                : Math.round(conformes * 10000.0 / totalControles) / 100.0;

        // Delai moyen de livraison = (date reelle - date prevue) sur les commandes livrees.
        double delaiMoyen = 0;
        if (!delivered.isEmpty()) {
            long somme = delivered.stream().mapToLong(Commande::getRetardJours).sum();
            delaiMoyen = Math.round(somme * 100.0 / delivered.size()) / 100.0;
        }

        List<OrderResponse> prioritaires = commandeRepository.findActiveOrdered().stream()
                .filter(c -> c.getPriorite() != PrioriteCommande.NORMALE)
                .limit(10)
                .map(c -> commandeMapper.toResponse(c))
                .toList();

        List<OrderResponse> retard = commandeRepository.findDelayed(LocalDate.now()).stream()
                .limit(10)
                .map(c -> commandeMapper.toResponse(c))
                .toList();

        return new DashboardResponse(total, enProduction, retards, delivered.size(), urgentes, chiffreAffaires,
                tauxConformite, delaiMoyen, repartition, prioritaires, retard);
    }
}