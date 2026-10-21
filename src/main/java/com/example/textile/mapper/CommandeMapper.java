package com.example.textile.mapper;

import com.example.textile.dto.request.CreateOrderRequest;
import com.example.textile.dto.request.OrderLineRequest;
import com.example.textile.dto.request.UpdateOrderRequest;
import com.example.textile.dto.response.*;
import com.example.textile.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandeMapper {

    private final ClientMapper clientMapper;

    public CommandeMapper(ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    /**
     * Construit une nouvelle commande a partir de la requete de creation.
     * Le numero est genere par le service.
     */
    public Commande toEntity(CreateOrderRequest request, Client client) {
        Commande commande = new Commande();
        commande.setClient(client);
        commande.setDateCommande(java.time.LocalDate.now());
        commande.setDateLivraisonPrevue(request.getDateLivraisonPrevue());
        commande.setPriorite(request.getPriorite() != null ? request.getPriorite() : PrioriteCommande.NORMALE);
        commande.setStatut(StatutCommande.BROUILLON);
        commande.setCommentaire(request.getCommentaire());
        commande.setInstructionsSpeciales(request.getInstructionsSpeciales());
        return commande;
    }

    public LigneCommande toLigneEntity(OrderLineRequest request, Produit produit) {
        return new LigneCommande(produit, request.getQuantite(), request.getTaille(), request.getCouleur(),
                request.getPrixUnitaire() != null ? request.getPrixUnitaire() : produit.getPrixUnitaire());
    }

    public void applyUpdate(Commande commande, UpdateOrderRequest request) {
        if (request.getDateLivraisonPrevue() != null) {
            commande.setDateLivraisonPrevue(request.getDateLivraisonPrevue());
        }
        if (request.getPriorite() != null) {
            commande.setPriorite(request.getPriorite());
        }
        if (request.getCommentaire() != null) {
            commande.setCommentaire(request.getCommentaire());
        }
        if (request.getInstructionsSpeciales() != null) {
            commande.setInstructionsSpeciales(request.getInstructionsSpeciales());
        }
    }

    public OrderResponse toResponse(Commande commande) {
        List<OrderLineResponse> lignes = commande.getLignes().stream()
                .map(this::toLigneResponse)
                .toList();
        List<StepResponse> etapes = commande.getEtapes().stream()
                .map(this::toStepResponse)
                .toList();
        return new OrderResponse(
                commande.getId(),
                commande.getNumero(),
                commande.getClient().getId(),
                commande.getClient().getName(),
                commande.getClient().getPhone(),
                commande.getDateCommande(),
                commande.getDateLivraisonPrevue(),
                commande.getDateLivraisonReelle(),
                commande.getPriorite(),
                commande.getStatut(),
                commande.getCommentaire(),
                commande.getInstructionsSpeciales(),
                commande.getMontantTotal(),
                commande.getQuantiteTotale(),
                commande.getQuantiteProduiteTotale(),
                commande.getDureePrevueJours(),
                commande.getDureeReelleJours(),
                commande.getRetardJours(),
                commande.isEnRetard(),
                commande.getEtapeCourante(),
                lignes,
                etapes,
                toControleResponse(commande.getControleQualite()));
    }

    public OrderLineResponse toLigneResponse(LigneCommande ligne) {
        return new OrderLineResponse(
                ligne.getId(),
                ligne.getProduit().getId(),
                ligne.getProduit().getCode(),
                ligne.getProduit().getDesignation(),
                ligne.getQuantite(),
                ligne.getTaille(),
                ligne.getCouleur(),
                ligne.getPrixUnitaire(),
                ligne.getMontant(),
                ligne.getQuantiteProduite(),
                ligne.getQuantite() - ligne.getQuantiteProduite());
    }

    public StepResponse toStepResponse(EtapeProduction etape) {
        return new StepResponse(
                etape.getId(),
                etape.getType(),
                etape.getType().getLibelle(),
                etape.getRang(),
                etape.getStatut(),
                etape.getResponsable() != null ? etape.getResponsable().getId() : null,
                etape.getResponsable() != null ? etape.getResponsable().getDisplayName() : null,
                etape.getDateDebut(),
                etape.getDateFinPrevue(),
                etape.getDateFinReelle(),
                etape.getCommentaire(),
                etape.getQuantiteProduite());
    }

    public QualityControlResponse toControleResponse(ControleQualite cq) {
        if (cq == null) {
            return null;
        }
        return new QualityControlResponse(
                cq.getId(),
                cq.getResultat(),
                cq.getControleur() != null ? cq.getControleur().getDisplayName() : null,
                cq.getDateControle(),
                cq.getObservations(),
                cq.getPointsNonConformites(),
                cq.getQuantiteControlee());
    }

    public ProductionResponse toProductionResponse(Commande commande) {
        List<StepResponse> etapes = commande.getEtapes().stream().map(this::toStepResponse).toList();
        int total = etapes.size();
        int termineesCount = (int) commande.getEtapes().stream().filter(EtapeProduction::isTerminee).count();
        TypeEtape courante = commande.getEtapeCourante();
        return new ProductionResponse(
                commande.getId(),
                commande.getNumero(),
                courante,
                courante != null ? courante.getRang() : total,
                termineesCount,
                total,
                total == 0 ? 0 : (int) Math.round(100.0 * termineesCount / total),
                commande.getQuantiteTotale(),
                commande.getQuantiteProduiteTotale(),
                commande.getDateLivraisonPrevue(),
                commande.getRetardJours(),
                commande.isEnRetard(),
                commande.getLignes().stream().map(this::toLigneResponse).toList(),
                etapes,
                toControleResponse(commande.getControleQualite()));
    }

    public DeliveryResponse toDeliveryResponse(Livraison livraison) {
        return new DeliveryResponse(
                livraison.getId(),
                livraison.getCommande().getNumero(),
                livraison.getDateLivraison(),
                livraison.getReceptionnaire(),
                livraison.getBonLivraison(),
                livraison.getCommentaire(),
                livraison.getQuantiteLivree(),
                livraison.getCommande().getRetardJours());
    }
}