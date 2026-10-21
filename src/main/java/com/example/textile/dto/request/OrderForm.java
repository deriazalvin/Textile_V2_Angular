package com.example.textile.dto.request;

import com.example.textile.entity.PrioriteCommande;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulaire de saisie d'une commande (interface Thymeleaf).
 * Les lignes sont saisies dynamiquement par l'utilisateur.
 */
public class OrderForm {

    private Long id;

    @NotNull(message = "Le client est obligatoire")
    private Long clientId;

    @NotNull(message = "La date prevue de livraison est obligatoire")
    @FutureOrPresent(message = "La date prevue de livraison ne peut pas etre dans le passe")
    private LocalDate dateLivraisonPrevue = LocalDate.now().plusDays(14);

    private PrioriteCommande priorite = PrioriteCommande.NORMALE;

    @Size(max = 500, message = "Le commentaire ne doit pas depasser 500 caracteres")
    private String commentaire;

    @Size(max = 500, message = "Les instructions speciales ne doivent pas depasser 500 caracteres")
    private String instructionsSpeciales;

    @Valid
    private List<LigneForm> lignes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<LigneForm> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneForm> lignes) {
        this.lignes = lignes;
    }

    /** Ajoute une ligne vide au formulaire. */
    public void addLigne() {
        lignes.add(new LigneForm());
    }

    public void removeLigne(int index) {
        if (index >= 0 && index < lignes.size()) {
            lignes.remove(index);
        }
    }

    public static class LigneForm {

        private Long id;

        @NotNull(message = "Le produit est obligatoire")
        private Long produitId;

        @NotNull(message = "La quantite est obligatoire")
        @Min(value = 1, message = "La quantite doit etre superieure a 0")
        private Integer quantite = 1;

        @Size(max = 10, message = "La taille est trop longue")
        private String taille;

        @Size(max = 40, message = "La couleur est trop longue")
        private String couleur;

        private java.math.BigDecimal prixUnitaire;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

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

        public java.math.BigDecimal getPrixUnitaire() {
            return prixUnitaire;
        }

        public void setPrixUnitaire(java.math.BigDecimal prixUnitaire) {
            this.prixUnitaire = prixUnitaire;
        }
    }
}