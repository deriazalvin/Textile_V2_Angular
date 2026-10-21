package com.example.textile.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Resultat du controle qualite, etape obligatoire avant livraison.
 * Une commande NON_CONFORME ne peut pas etre livree.
 */
@Entity
@Table(name = "controles_qualite")
public class ControleQualite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commande_id", nullable = false, unique = true)
    private Commande commande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultatControle resultat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "controleur_id")
    private AppUser controleur;

    @Column(nullable = false)
    private LocalDateTime dateControle;

    @Column(length = 800)
    private String observations;

    @Column(length = 500)
    private String pointsNonConformites;

    @Column(nullable = false)
    private Integer quantiteControlee = 0;

    protected ControleQualite() {
    }

    public ControleQualite(Commande commande, ResultatControle resultat, AppUser controleur, String observations) {
        this.commande = commande;
        this.resultat = resultat;
        this.controleur = controleur;
        this.observations = observations;
        this.dateControle = LocalDateTime.now();
    }

    public boolean isConforme() {
        return resultat == ResultatControle.CONFORME;
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

    public ResultatControle getResultat() {
        return resultat;
    }

    public void setResultat(ResultatControle resultat) {
        this.resultat = resultat;
    }

    public AppUser getControleur() {
        return controleur;
    }

    public void setControleur(AppUser controleur) {
        this.controleur = controleur;
    }

    public LocalDateTime getDateControle() {
        return dateControle;
    }

    public void setDateControle(LocalDateTime dateControle) {
        this.dateControle = dateControle;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getPointsNonConformites() {
        return pointsNonConformites;
    }

    public void setPointsNonConformites(String pointsNonConformites) {
        this.pointsNonConformites = pointsNonConformites;
    }

    public Integer getQuantiteControlee() {
        return quantiteControlee;
    }

    public void setQuantiteControlee(Integer quantiteControlee) {
        this.quantiteControlee = quantiteControlee;
    }
}