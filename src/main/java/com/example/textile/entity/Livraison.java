package com.example.textile.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Preuve de livraison : cloture finale d'une commande.
 */
@Entity
@Table(name = "livraisons")
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commande_id", nullable = false, unique = true)
    private Commande commande;

    @Column(nullable = false)
    private LocalDate dateLivraison;

    /** Nom du receptionnaire signe. */
    @Column(nullable = false, length = 120)
    private String receptionnaire;

    @Column(length = 60)
    private String bonLivraison;

    @Column(length = 500)
    private String commentaire;

    @Column(nullable = false)
    private Integer quantiteLivree = 0;

    protected Livraison() {
    }

    public Livraison(Commande commande, String receptionnaire, LocalDate dateLivraison, String bonLivraison,
                     String commentaire) {
        this.commande = commande;
        this.receptionnaire = receptionnaire;
        this.dateLivraison = dateLivraison;
        this.bonLivraison = bonLivraison;
        this.commentaire = commentaire;
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

    public LocalDate getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(LocalDate dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public String getReceptionnaire() {
        return receptionnaire;
    }

    public void setReceptionnaire(String receptionnaire) {
        this.receptionnaire = receptionnaire;
    }

    public String getBonLivraison() {
        return bonLivraison;
    }

    public void setBonLivraison(String bonLivraison) {
        this.bonLivraison = bonLivraison;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Integer getQuantiteLivree() {
        return quantiteLivree;
    }

    public void setQuantiteLivree(Integer quantiteLivree) {
        this.quantiteLivree = quantiteLivree;
    }
}