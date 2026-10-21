package com.example.textile.dto.response;

import com.example.textile.entity.ResultatControle;

import java.time.LocalDateTime;

public record QualityControlResponse(
        Long id,
        ResultatControle resultat,
        String controleurNom,
        LocalDateTime dateControle,
        String observations,
        String pointsNonConformites,
        Integer quantiteControlee) {
}