package com.example.textile.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Commande textile et son workflow de production.
 */
@Entity
@Table(name = "commandes")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String numero;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false)
    private LocalDate dateCommande;

    /** Date prevue de livraison au plus tard. */
    @Column(nullable = false)
    private LocalDate dateLivraisonPrevue;

    /** Date reelle de livraison (renseignee a la livraison). */
    private LocalDate dateLivraisonReelle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioriteCommande priorite = PrioriteCommande.NORMALE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCommande statut = StatutCommande.BROUILLON;

    @Column(length = 500)
    private String commentaire;

    @Column(length = 500)
    private String instructionsSpeciales;

    /** Date de fin prevue de la production. */
    private LocalDate dateFinProductionPrevue;

    private LocalDate dateFinProductionReelle;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private java.util.List<LigneCommande> lignes = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("rang ASC")
    private java.util.List<EtapeProduction> etapes = new java.util.ArrayList<>();

    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ControleQualite controleQualite;

    /** Constructeur pour les ORM et le mapping. */
    public Commande() {
    }

    public Commande(String numero, Client client, LocalDate dateCommande, LocalDate dateLivraisonPrevue,
                    PrioriteCommande priorite, String commentaire) {
        this.numero = numero;
        this.client = client;
        this.dateCommande = dateCommande;
        this.dateLivraisonPrevue = dateLivraisonPrevue;
        this.priorite = priorite;
        this.commentaire = commentaire;
    }

    public void addLigne(LigneCommande ligne) {
        ligne.setCommande(this);
        this.lignes.add(ligne);
    }

    public void addEtape(EtapeProduction etape) {
        etape.setCommande(this);
        this.etapes.add(etape);
    }

    public BigDecimal getMontantTotal() {
        return lignes.stream()
                .map(LigneCommande::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getQuantiteTotale() {
        return lignes.stream().mapToInt(LigneCommande::getQuantite).sum();
    }

    public int getQuantiteProduiteTotale() {
        return lignes.stream().mapToInt(LigneCommande::getQuantiteProduite).sum();
    }

    /** Duree prevue en jours (dateLivraisonPrevue - dateCommande). */
    public long getDureePrevueJours() {
        return ChronoUnit.DAYS.between(dateCommande, dateLivraisonPrevue);
    }

    /** Duree reelle en jours, ou {@code null} si la commande n'est pas encore livree. */
    public Long getDureeReelleJours() {
        if (dateLivraisonReelle == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(dateCommande, dateLivraisonReelle);
    }

    /** Retard en jours : positif = en retard, negatif = en avance. */
    public long getRetardJours() {
        LocalDate ref = dateLivraisonReelle != null ? dateLivraisonReelle : LocalDate.now();
        return ChronoUnit.DAYS.between(dateLivraisonPrevue, ref);
    }

    /** Vrai si la commande n'est pas encore livree et que la date prevue est depassee. */
    public boolean isEnRetard() {
        return statut != StatutCommande.LIVREE
                && statut != StatutCommande.ANNULEE
                && dateLivraisonPrevue != null
                && dateLivraisonPrevue.isBefore(LocalDate.now());
    }

    public boolean isTerminee() {
        return statut == StatutCommande.LIVREE || statut == StatutCommande.ANNULEE;
    }

    public boolean isModifiable() {
        return statut != StatutCommande.LIVREE;
    }

    public EtapeProduction getEtape(TypeEtape type) {
        return etapes.stream().filter(e -> e.getType() == type).findFirst().orElse(null);
    }

    /** Etape courante = premiere etape non terminee. */
    public TypeEtape getEtapeCourante() {
        for (EtapeProduction e : etapes) {
            if (e.getStatut() != StatutEtape.TERMINEE) {
                return e.getType();
            }
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public LocalDate getDateLivraisonPrevue() {
        return dateLivraisonPrevue;
    }

    public void setDateLivraisonPrevue(LocalDate dateLivraisonPrevue) {
        this.dateLivraisonPrevue = dateLivraisonPrevue;
    }

    public LocalDate getDateLivraisonReelle() {
        return dateLivraisonReelle;
    }

    public void setDateLivraisonReelle(LocalDate dateLivraisonReelle) {
        this.dateLivraisonReelle = dateLivraisonReelle;
    }

    public PrioriteCommande getPriorite() {
        return priorite;
    }

    public void setPriorite(PrioriteCommande priorite) {
        this.priorite = priorite;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
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

    public LocalDate getDateFinProductionPrevue() {
        return dateFinProductionPrevue;
    }

    public void setDateFinProductionPrevue(LocalDate dateFinProductionPrevue) {
        this.dateFinProductionPrevue = dateFinProductionPrevue;
    }

    public LocalDate getDateFinProductionReelle() {
        return dateFinProductionReelle;
    }

    public void setDateFinProductionReelle(LocalDate dateFinProductionReelle) {
        this.dateFinProductionReelle = dateFinProductionReelle;
    }

    public java.util.List<LigneCommande> getLignes() {
        return lignes;
    }

    public void setLignes(java.util.List<LigneCommande> lignes) {
        this.lignes = lignes;
    }

    public java.util.List<EtapeProduction> getEtapes() {
        return etapes;
    }

    public void setEtapes(java.util.List<EtapeProduction> etapes) {
        this.etapes = etapes;
    }

    public ControleQualite getControleQualite() {
        return controleQualite;
    }

    public void setControleQualite(ControleQualite controleQualite) {
        this.controleQualite = controleQualite;
    }
}