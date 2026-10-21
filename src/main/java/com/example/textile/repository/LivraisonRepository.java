package com.example.textile.repository;

import com.example.textile.entity.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LivraisonRepository extends JpaRepository<Livraison, Long> {

    Optional<Livraison> findByCommandeId(Long commandeId);
}