package com.example.textile.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Ligne de commande : produit, quantite, taille, couleur, prix unitaire,
 * et quantite reellement produite (mise a jour a chaque etape).
 */
@Entity
@Table(name = "lignes_commande")
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer quantite = 1;

    @jakarta.validation.constraints.Size(max = 10)
    private String taille;

    @jakarta.validation.constraints.Size(max = 40)
    private String couleur;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire = BigDecimal.ZERO;

    /** Quantite effectivement declaree produite a la derniere etape terminee. */
    @Min(0)
    @Column(nullable = false)
    private Integer quantiteProduite = 0;

    protected LigneCommande() {
    }

    public LigneCommande(Produit produit, Integer quantite, String taille, String couleur, BigDecimal prixUnitaire) {
        this.produit = produit;
        this.quantite = quantite;
        this.taille = taille;
        this.couleur = couleur;
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getMontant() {
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
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

    public Integer getQuantiteProduite() {
        return quantiteProduite;
    }

    public void setQuantiteProduite(Integer quantiteProduite) {
        this.quantiteProduite = quantiteProduite;
    }
}