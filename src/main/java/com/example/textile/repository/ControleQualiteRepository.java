package com.example.textile.repository;

import com.example.textile.entity.ControleQualite;
import com.example.textile.entity.ResultatControle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ControleQualiteRepository extends JpaRepository<ControleQualite, Long> {

    Optional<ControleQualite> findByCommandeId(Long commandeId);

    long countByResultat(ResultatControle resultat);

    }