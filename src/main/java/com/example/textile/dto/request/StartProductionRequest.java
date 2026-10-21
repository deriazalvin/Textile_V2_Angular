package com.example.textile.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Demarrage de la production d'une commande : creation des etapes du workflow.
 */
public class StartProductionRequest {

    private Long responsableId;

    @Size(max = 500, message = "Le commentaire ne doit pas depasser 500 caracteres")
    private String commentaire;

    public Long getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Long responsableId) {
        this.responsableId = responsableId;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}