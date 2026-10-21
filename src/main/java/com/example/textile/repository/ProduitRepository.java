package com.example.textile.repository;

import com.example.textile.entity.Produit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    @Query("""
            SELECT p FROM Produit p
            WHERE (:search IS NULL OR LOWER(p.designation) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(p.code) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR LOWER(p.categorie) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:categorie IS NULL OR LOWER(p.categorie) = LOWER(:categorie))
              AND (:active IS NULL OR p.active = :active)
            """)
    Page<Produit> search(@Param("search") String search,
                         @Param("categorie") String categorie,
                         @Param("active") Boolean active,
                         Pageable pageable);
}