package com.example.textile.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class UpdateProduitRequest {

    @Size(max = 150, message = "La designation ne doit pas depasser 150 caracteres")
    private String designation;

    @Size(max = 80, message = "La categorie ne doit pas depasser 80 caracteres")
    private String categorie;

    @Size(max = 80, message = "La matiere ne doit pas depasser 80 caracteres")
    private String matiere;

    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix unitaire ne peut pas etre negatif")
    private BigDecimal prixUnitaire;

    @Min(value = 0, message = "Le stock ne peut pas etre negatif")
    private Integer stock;

    private Boolean active;

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}