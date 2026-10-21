package com.example.textile.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateProduitRequest {

    @NotBlank(message = "Le code produit est obligatoire")
    @Size(max = 40, message = "Le code ne doit pas depasser 40 caracteres")
    private String code;

    @NotBlank(message = "La designation est obligatoire")
    @Size(max = 150, message = "La designation ne doit pas depasser 150 caracteres")
    private String designation;

    @Size(max = 80, message = "La categorie ne doit pas depasser 80 caracteres")
    private String categorie;

    @Size(max = 80, message = "La matiere ne doit pas depasser 80 caracteres")
    private String matiere;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix unitaire ne peut pas etre negatif")
    private BigDecimal prixUnitaire;

    @Min(value = 0, message = "Le stock ne peut pas etre negatif")
    private Integer stock = 0;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getMatiere() {
        return matiere;
    }

    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}