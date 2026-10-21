package com.example.textile.dto.request;

import com.example.textile.entity.PrioriteCommande;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderRequest {

    @NotNull(message = "Le client est obligatoire")
    private Long clientId;

    @NotNull(message = "La date prevue de livraison est obligatoire")
    @FutureOrPresent(message = "La date prevue de livraison ne peut pas etre dans le passe")
    private LocalDate dateLivraisonPrevue;

    private PrioriteCommande priorite = PrioriteCommande.NORMALE;

    @Size(max = 500, message = "Le commentaire ne doit pas depasser 500 caracteres")
    private String commentaire;

    @Size(max = 500, message = "Les instructions speciales ne doivent pas depasser 500 caracteres")
    private String instructionsSpeciales;

    @NotEmpty(message = "La commande doit contenir au moins une ligne")
    @Valid
    private List<OrderLineRequest> lignes = new ArrayList<>();

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public LocalDate getDateLivraisonPrevue() {
        return dateLivraisonPrevue;
    }

    public void setDateLivraisonPrevue(LocalDate dateLivraisonPrevue) {
        this.dateLivraisonPrevue = dateLivraisonPrevue;
    }

    public PrioriteCommande getPriorite() {
        return priorite;
    }

    public void setPriorite(PrioriteCommande priorite) {
        this.priorite = priorite;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getInstructionsSpeciales() {
        return instructionsSpeciales;
    }

    public void setInstructionsSpeciales(String instructionsSpeciales) {
        this.instructionsSpeciales = instructionsSpeciales;
    }

    public List<OrderLineRequest> getLignes() {
        return lignes;
    }

    public void setLignes(List<OrderLineRequest> lignes) {
        this.lignes = lignes;
    }
}