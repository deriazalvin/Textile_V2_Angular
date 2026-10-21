package com.example.textile.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class OrderLineRequest {

    @NotNull(message = "Le produit est obligatoire")
    private Long produitId;

    @NotNull(message = "La quantite est obligatoire")
    @Positive(message = "La quantite doit etre strictement positive")
    private Integer quantite;

    @Size(max = 10, message = "La taille ne doit pas depasser 10 caracteres")
    private String taille;

    @Size(max = 40, message = "La couleur ne doit pas depasser 40 caracteres")
    private String couleur;

    /** Prix unitaire : facultatif, repris du catalogue si absent. */
    private BigDecimal prixUnitaire;

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public String getTaille() {
        return taille;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
}