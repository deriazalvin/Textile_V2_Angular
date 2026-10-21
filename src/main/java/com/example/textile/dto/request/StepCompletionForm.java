package com.example.textile.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Formulaire de cloture d'une etape de production (interface Thymeleaf).
 */
public class StepCompletionForm {

    @NotNull(message = "L'etape est obligatoire")
    private String etape;

    @Size(max = 500, message = "Le commentaire ne doit pas depasser 500 caracteres")
    private String commentaire;

    private List<QuantiteLigneForm> quantites = new ArrayList<>();

    public String getEtape() {
        return etape;
    }

    public void setEtape(String etape) {
        this.etape = etape;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public List<QuantiteLigneForm> getQuantites() {
        return quantites;
    }

    public void setQuantites(List<QuantiteLigneForm> quantites) {
        this.quantites = quantites;
    }

    public static class QuantiteLigneForm {

        private Long ligneId;

        private String libelle;

        @NotNull(message = "La quantite produite est obligatoire")
        @Min(value = 0, message = "La quantite ne peut pas etre negative")
        @Max(value = 1_000_000, message = "La quantite est trop elevee")
        private Integer quantite;

        private Integer quantiteDisponible;

        public Long getLigneId() {
            return ligneId;
        }

        public void setLigneId(Long ligneId) {
            this.ligneId = ligneId;
        }

        public String getLibelle() {
            return libelle;
        }

        public void setLibelle(String libelle) {
            this.libelle = libelle;
        }

        public Integer getQuantite() {
            return quantite;
        }

        public void setQuantite(Integer quantite) {
            this.quantite = quantite;
        }

        public Integer getQuantiteDisponible() {
            return quantiteDisponible;
        }

        public void setQuantiteDisponible(Integer quantiteDisponible) {
            this.quantiteDisponible = quantiteDisponible;
        }
    }
}