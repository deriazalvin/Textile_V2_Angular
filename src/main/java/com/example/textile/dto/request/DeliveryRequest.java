package com.example.textile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Preuve de livraison : cloture definitive de la commande.
 */
public class DeliveryRequest {

    @NotBlank(message = "Le nom du receptionnaire est obligatoire")
    @Size(max = 120, message = "Le nom du receptionnaire ne doit pas depasser 120 caracteres")
    private String receptionnaire;

    @Size(max = 60, message = "Le bon de livraison ne doit pas depasser 60 caracteres")
    private String bonLivraison;

    @Size(max = 500, message = "Le commentaire ne doit pas depasser 500 caracteres")
    private String commentaire;

    public String getReceptionnaire() {
        return receptionnaire;
    }

    public void setReceptionnaire(String receptionnaire) {
        this.receptionnaire = receptionnaire;
    }

    public String getBonLivraison() {
        return bonLivraison;
    }

    public void setBonLivraison(String bonLivraison) {
        this.bonLivraison = bonLivraison;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}