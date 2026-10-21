package com.example.textile.mapper;

import com.example.textile.dto.request.CreateProduitRequest;
import com.example.textile.dto.request.UpdateProduitRequest;
import com.example.textile.dto.response.ProduitResponse;
import com.example.textile.entity.Produit;
import org.springframework.stereotype.Component;

@Component
public class ProduitMapper {

    public Produit toEntity(CreateProduitRequest request) {
        Produit produit = new Produit(request.getCode(), request.getDesignation(), request.getCategorie(),
                request.getMatiere(), request.getPrixUnitaire());
        produit.setStock(request.getStock() == null ? 0 : request.getStock());
        return produit;
    }

    public void updateEntity(Produit produit, UpdateProduitRequest request) {
        if (request.getDesignation() != null && !request.getDesignation().isBlank()) {
            produit.setDesignation(request.getDesignation());
        }
        if (request.getCategorie() != null) {
            produit.setCategorie(request.getCategorie());
        }
        if (request.getMatiere() != null) {
            produit.setMatiere(request.getMatiere());
        }
        if (request.getPrixUnitaire() != null) {
            produit.setPrixUnitaire(request.getPrixUnitaire());
        }
        if (request.getStock() != null) {
            produit.setStock(request.getStock());
        }
        if (request.getActive() != null) {
            produit.setActive(request.getActive());
        }
    }

    public ProduitResponse toResponse(Produit produit) {
        return new ProduitResponse(
                produit.getId(),
                produit.getCode(),
                produit.getDesignation(),
                produit.getCategorie(),
                produit.getMatiere(),
                produit.getPrixUnitaire(),
                produit.getStock(),
                produit.isActive());
    }
}