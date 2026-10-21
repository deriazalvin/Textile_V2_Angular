package com.example.textile.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class CompleteStepRequest {

    @NotEmpty(message = "Les quantites produites par ligne sont obligatoires")
    private List<StepLineQuantity> quantites = new ArrayList<>();

    @Size(max = 500, message = "Le commentaire ne doit pas depasser 500 caracteres")
    private String commentaire;

    public List<StepLineQuantity> getQuantites() {
        return quantites;
    }

    public void setQuantites(List<StepLineQuantity> quantites) {
        this.quantites = quantites;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public static class StepLineQuantity {

        @jakarta.validation.constraints.NotNull(message = "La ligne de commande est obligatoire")
        private Long ligneId;

        @jakarta.validation.constraints.NotNull(message = "La quantite produite est obligatoire")
        @Min(value = 0, message = "La quantite produite ne peut pas etre negative")
        @Max(value = 1_000_000, message = "La quantite produite est trop elevee")
        private Integer quantite;

        public Long getLigneId() {
            return ligneId;
        }

        public void setLigneId(Long ligneId) {
            this.ligneId = ligneId;
        }

        public Integer getQuantite() {
            return quantite;
        }

        public void setQuantite(Integer quantite) {
            this.quantite = quantite;
        }
    }
}