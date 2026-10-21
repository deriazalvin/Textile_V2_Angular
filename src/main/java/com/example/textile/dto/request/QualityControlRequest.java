package com.example.textile.dto.request;

import com.example.textile.entity.ResultatControle;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Controle qualite : etape obligatoire avant livraison.
 */
public class QualityControlRequest {

    @NotNull(message = "Le resultat du controle est obligatoire")
    private ResultatControle resultat;

    private Long controleurId;

    @Size(max = 800, message = "Les observations ne doivent pas depasser 800 caracteres")
    private String observations;

    @Size(max = 500, message = "Les points de non conformite ne doivent pas depasser 500 caracteres")
    private String pointsNonConformites;

    public ResultatControle getResultat() {
        return resultat;
    }

    public void setResultat(ResultatControle resultat) {
        this.resultat = resultat;
    }

    public Long getControleurId() {
        return controleurId;
    }

    public void setControleurId(Long controleurId) {
        this.controleurId = controleurId;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getPointsNonConformites() {
        return pointsNonConformites;
    }

    public void setPointsNonConformites(String pointsNonConformites) {
        this.pointsNonConformites = pointsNonConformites;
    }
}