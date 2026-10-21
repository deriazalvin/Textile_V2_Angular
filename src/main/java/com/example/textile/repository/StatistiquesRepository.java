package com.example.textile.repository;

import com.example.textile.entity.Commande;
import com.example.textile.entity.EtapeProduction;
import com.example.textile.entity.LigneCommande;
import com.example.textile.entity.ResultatControle;
import com.example.textile.entity.StatutEtape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Requetes d'agregation pour les statistiques de production.
 */
public interface StatistiquesRepository extends JpaRepository<Commande, Long> {

    /** Quantites commandees / produites par produit. */
    @Query("""
            SELECT l.produit.code,
                   l.produit.designation,
                   COALESCE(SUM(l.quantite), 0),
                   COALESCE(SUM(l.quantiteProduite), 0)
            FROM LigneCommande l
            GROUP BY l.produit.id, l.produit.code, l.produit.designation
            ORDER BY COALESCE(SUM(l.quantite), 0) DESC
            """)
    List<Object[]> quantitesParProduit();

    /** Etapes terminees et quantites produites par type d'etape. */
    @Query("""
            SELECT e.type,
                   COUNT(e),
                   COALESCE(SUM(e.quantiteProduite), 0)
            FROM EtapeProduction e
            WHERE e.statut = :statut
            GROUP BY e.type, e.rang
            ORDER BY MIN(e.rang)
            """)
    List<Object[]> etapesParType(@Param("statut") StatutEtape statut);

    /** Volume et montant par client. */
    @Query("""
            SELECT c.client.name,
                   COUNT(DISTINCT c.id),
                   COALESCE(SUM(l.quantite * l.prixUnitaire), 0)
            FROM Commande c JOIN c.lignes l
            GROUP BY c.client.id, c.client.name
            ORDER BY COALESCE(SUM(l.quantite * l.prixUnitaire), 0) DESC
            """)
    List<Object[]> volumeParClient();

    /** Quantite commandee totale. */
    @Query("SELECT COALESCE(SUM(l.quantite), 0) FROM LigneCommande l")
    long quantiteCommandeeTotale();

    /** Quantite produite totale. */
    @Query("SELECT COALESCE(SUM(l.quantiteProduite), 0) FROM LigneCommande l")
    long quantiteProduiteTotale();

    /** Commandes livrees dans les delais. */
    @Query("""
            SELECT COUNT(c) FROM Commande c
            WHERE c.statut = com.example.textile.entity.StatutCommande.LIVREE
              AND (c.dateLivraisonReelle IS NULL OR c.dateLivraisonReelle <= c.dateLivraisonPrevue)
            """)
    long countDeliveredOnTime();

    /** Commandes livrees hors delai. */
    @Query("""
            SELECT COUNT(c) FROM Commande c
            WHERE c.statut = com.example.textile.entity.StatutCommande.LIVREE
              AND c.dateLivraisonReelle > c.dateLivraisonPrevue
            """)
    long countDeliveredLate();

    /** Nombre de controles par resultat. */
    @Query("SELECT COUNT(c) FROM ControleQualite c WHERE c.resultat = :resultat")
    long countControlesParResultat(@Param("resultat") ResultatControle resultat);

    /** Toutes les lignes de commande, pour le calcul de rendement. */
    @Query("SELECT l FROM LigneCommande l")
    List<LigneCommande> findAllLignes();

    /** Toutes les etapes de production, pour le calcul de rendement par etape. */
    @Query("SELECT e FROM EtapeProduction e")
    List<EtapeProduction> findAllEtapes();
}