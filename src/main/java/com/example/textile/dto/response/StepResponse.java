package com.example.textile.dto.response;

import com.example.textile.entity.StatutEtape;
import com.example.textile.entity.TypeEtape;

import java.time.LocalDateTime;

public record StepResponse(
        Long id,
        TypeEtape type,
        String typeLibelle,
        int rang,
        StatutEtape statut,
        Long responsableId,
        String responsableNom,
        LocalDateTime dateDebut,
        LocalDateTime dateFinPrevue,
        LocalDateTime dateFinReelle,
        String commentaire,
        Integer quantiteProduite) {
}