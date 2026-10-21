package com.example.textile.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Une etape du workflow de production d'une commande.
 * Chaque etape possede un statut, un responsable, des dates et un commentaire.
 */
@Entity
@Table(name = "etapes_production",
        uniqueConstraints = @UniqueConstraint(name = "uk_commande_etape", columnNames = {"commande_id", "type_etape"}))
public class EtapeProduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_etape", nullable = false, length = 25)
    private TypeEtape type;

    /** Rang de l'etape dans le workflow (0 = COMMANDE ... 5 = LIVRAISON). */
    @Column(nullable = false)
    private int rang;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutEtape statut = StatutEtape.NON_COMMENCEE;

    /** Utilisateur responsable de l'etape. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "responsable_id")
    private AppUser responsable;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFinPrevue;

    private LocalDateTime dateFinReelle;

    @Column(length = 500)
    private String commentaire;

    /** Quantite declaree produite a la cloture de l'etape (somme des lignes). */
    private Integer quantiteProduite = 0;

    protected EtapeProduction() {
    }

    public EtapeProduction(Commande commande, TypeEtape type, LocalDateTime dateFinPrevue) {
        this.commande = commande;
        this.type = type;
        this.rang = type.getRang();
        this.dateFinPrevue = dateFinPrevue;
        this.quantiteProduite = 0;
    }

    public boolean isTerminee() {
        return statut == StatutEtape.TERMINEE;
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

    public TypeEtape getType() {
        return type;
    }

    public void setType(TypeEtape type) {
        this.type = type;
        this.rang = type.getRang();
    }

    public int getRang() {
        return rang;
    }

    public void setRang(int rang) {
        this.rang = rang;
    }

    public StatutEtape getStatut() {
        return statut;
    }

    public void setStatut(StatutEtape statut) {
        this.statut = statut;
    }

    public AppUser getResponsable() {
        return responsable;
    }

    public void setResponsable(AppUser responsable) {
        this.responsable = responsable;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFinPrevue() {
        return dateFinPrevue;
    }

    public void setDateFinPrevue(LocalDateTime dateFinPrevue) {
        this.dateFinPrevue = dateFinPrevue;
    }

    public LocalDateTime getDateFinReelle() {
        return dateFinReelle;
    }

    public void setDateFinReelle(LocalDateTime dateFinReelle) {
        this.dateFinReelle = dateFinReelle;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Integer getQuantiteProduite() {
        return quantiteProduite;
    }

    public void setQuantiteProduite(Integer quantiteProduite) {
        this.quantiteProduite = quantiteProduite;
    }
}