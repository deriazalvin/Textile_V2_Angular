package com.example.textile.repository;

import com.example.textile.entity.Commande;
import com.example.textile.entity.PrioriteCommande;
import com.example.textile.entity.StatutCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    Optional<Commande> findByNumero(String numero);

    boolean existsByNumero(String numero);

    long countByClientId(Long clientId);

    long countByClientIdAndStatut(Long clientId, StatutCommande statut);

    @Query("SELECT COUNT(c) > 0 FROM Commande c JOIN c.lignes l WHERE l.produit.id = :produitId")
    boolean existsByProduitId(@Param("produitId") Long produitId);

    long countByStatut(StatutCommande statut);

    long countByPriorite(PrioriteCommande priorite);

    long countByDateLivraisonPrevueBeforeAndStatutNotIn(LocalDate date, List<StatutCommande> statuts);

    @Query(value = """
            SELECT c FROM Commande c
            LEFT JOIN FETCH c.client
            WHERE (:statut IS NULL OR c.statut = :statut)
              AND (:priorite IS NULL OR c.priorite = :priorite)
              AND (:clientId IS NULL OR c.client.id = :clientId)
              AND (:search IS NULL OR LOWER(c.numero) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(c.client.name) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:delayedOnly = false OR (c.statut <> com.example.textile.entity.StatutCommande.LIVREE
                        AND c.statut <> com.example.textile.entity.StatutCommande.ANNULEE
                        AND c.dateLivraisonPrevue < :today))
            """,
            countQuery = """
            SELECT COUNT(c) FROM Commande c
            WHERE (:statut IS NULL OR c.statut = :statut)
              AND (:priorite IS NULL OR c.priorite = :priorite)
              AND (:clientId IS NULL OR c.client.id = :clientId)
              AND (:search IS NULL OR LOWER(c.numero) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(c.client.name) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:delayedOnly = false OR (c.statut <> com.example.textile.entity.StatutCommande.LIVREE
                        AND c.statut <> com.example.textile.entity.StatutCommande.ANNULEE
                        AND c.dateLivraisonPrevue < :today))
            """)
    Page<Commande> search(@Param("statut") StatutCommande statut,
                          @Param("priorite") PrioriteCommande priorite,
                          @Param("clientId") Long clientId,
                          @Param("search") String search,
                          @Param("delayedOnly") boolean delayedOnly,
                          @Param("today") LocalDate today,
                          Pageable pageable);

    /** Commandes actives triees par priorite puis par urgence de livraison. */
    @Query("""
            SELECT c FROM Commande c
            LEFT JOIN FETCH c.client
            WHERE c.statut NOT IN (com.example.textile.entity.StatutCommande.LIVREE,
                                   com.example.textile.entity.StatutCommande.ANNULEE)
            ORDER BY c.priorite DESC, c.dateLivraisonPrevue ASC
            """)
    List<Commande> findActiveOrdered();

    /** Commandes en retard, les plus urgentes d'abord. */
    @Query("""
            SELECT c FROM Commande c
            LEFT JOIN FETCH c.client
            WHERE c.statut NOT IN (com.example.textile.entity.StatutCommande.LIVREE,
                                   com.example.textile.entity.StatutCommande.ANNULEE)
              AND c.dateLivraisonPrevue < :today
            ORDER BY c.dateLivraisonPrevue ASC
            """)
    List<Commande> findDelayed(@Param("today") LocalDate today);

    @Query("SELECT c FROM Commande c WHERE c.statut = com.example.textile.entity.StatutCommande.LIVREE")
    List<Commande> findDelivered();
}