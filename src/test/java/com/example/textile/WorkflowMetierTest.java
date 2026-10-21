package com.example.textile;

import com.example.textile.dto.request.*;
import com.example.textile.dto.response.OrderResponse;
import com.example.textile.dto.response.ProductionResponse;
import com.example.textile.entity.*;
import com.example.textile.exception.BusinessException;
import com.example.textile.exception.InvalidOperationException;
import com.example.textile.service.ClientService;
import com.example.textile.service.CommandeService;
import com.example.textile.service.ProduitService;
import com.example.textile.service.ProductionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
class WorkflowMetierTest {

    @Autowired
    private ClientService clientService;
    @Autowired
    private ProduitService produitService;
    @Autowired
    private CommandeService commandeService;
    @Autowired
    private ProductionService productionService;

    private Long clientId;
    private Long produitId;

    @BeforeEach
    void preparerReferentiel() {
        clientId = clientService.create(new CreateClientRequest() {
            {
                setName("Client Test " + System.nanoTime());
                setPhone("+216 9" + (System.nanoTime() % 10000000));
                setEmail("test" + System.nanoTime() + "@exemple.tn");
                setCity("Sousse");
            }
        }).id();

        produitId = produitService.create(new CreateProduitRequest() {
            {
                setCode("TST-" + (System.nanoTime() % 100000));
                setDesignation("Chemise test");
                setCategorie("Chemises");
                setMatiere("Coton");
                setPrixUnitaire(new BigDecimal("40.00"));
                setStock(1000);
            }
        }).id();
    }

    private CreateOrderRequest creerCommande(int quantite) {
        OrderLineRequest ligne = new OrderLineRequest();
        ligne.setProduitId(produitId);
        ligne.setQuantite(quantite);
        ligne.setTaille("M");
        ligne.setCouleur("Blanc");
        CreateOrderRequest request = new CreateOrderRequest();
        request.setClientId(clientId);
        request.setDateLivraisonPrevue(LocalDate.now().plusDays(15));
        request.setPriorite(PrioriteCommande.URGENTE);
        request.setLignes(List.of(ligne));
        return request;
    }

    private CompleteStepRequest quantites(OrderResponse commande, Integer... valeurs) {
        CompleteStepRequest request = new CompleteStepRequest();
        for (int i = 0; i < valeurs.length; i++) {
            CompleteStepRequest.StepLineQuantity item = new CompleteStepRequest.StepLineQuantity();
            item.setLigneId(commande.lignes().get(i).id());
            item.setQuantite(valeurs[i]);
            request.getQuantites().add(item);
        }
        return request;
    }

    @Test
    @DisplayName("Une commande creee est en BROUILLON et n'a pas d'etapes")
    void creationCommande() {
        OrderResponse commande = commandeService.create(creerCommande(500));
        assertThat(commande.id()).isNotNull();
        assertThat(commande.numero()).startsWith("CMD-");
        assertThat(commande.statut()).isEqualTo(StatutCommande.BROUILLON);
        assertThat(commande.etapes()).isEmpty();
        assertThat(commande.quantiteTotale()).isEqualTo(500);
        assertThat(commande.priorite()).isEqualTo(PrioriteCommande.URGENTE);
    }

    @Test
    @DisplayName("Le demarrage de la production cree les 6 etapes du workflow")
    void demarrageProduction() {
        OrderResponse commande = commandeService.create(creerCommande(500));
        OrderResponse lancee = productionService.startProduction(commande.id(), new StartProductionRequest());

        assertThat(lancee.statut()).isEqualTo(StatutCommande.EN_PRODUCTION);
        assertThat(lancee.etapes()).hasSize(6);
        assertThat(lancee.etapes().get(0).type()).isEqualTo(TypeEtape.COMMANDE);
        assertThat(lancee.etapes().get(0).statut()).isEqualTo(StatutEtape.TERMINEE);
        assertThat(lancee.etapes().get(1).type()).isEqualTo(TypeEtape.COUPE);
        assertThat(lancee.etapes().get(1).statut()).isEqualTo(StatutEtape.EN_COURS);
        assertThat(lancee.etapeCourante()).isEqualTo(TypeEtape.COUPE);
        assertThat(lancee.quantiteProduiteTotale()).isEqualTo(500);
    }

    @Test
    @DisplayName("On ne peut pas lancer deux fois la production d'une commande")
    void productionNonRepetable() {
        OrderResponse commande = commandeService.create(creerCommande(500));
        productionService.startProduction(commande.id(), new StartProductionRequest());

        assertThatThrownBy(() -> productionService.startProduction(commande.id(), new StartProductionRequest()))
                .isInstanceOf(com.example.textile.exception.ConflictException.class)
                .hasMessageContaining(" deja ete demarree");
    }

    @Test
    @DisplayName("Impossible de cloturer une etape dont la precedente n'est pas terminee")
    void ordreEtapesRespecte() {
        OrderResponse commande = commandeService.create(creerCommande(500));
        productionService.startProduction(commande.id(), new StartProductionRequest());

        // La couture ne peut pas demarrer avant la fin de la coupe.
        assertThatThrownBy(() -> productionService.startStep(commande.id(), TypeEtape.COUTURE, null))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Coupe n'est pas terminee");
    }

    @Test
    @DisplayName("Scenario complet 500 / 480 / 450 avec controle qualite et livraison")
    void workflowComplet() {
        OrderResponse commande = commandeService.create(creerCommande(500));
        productionService.startProduction(commande.id(), new StartProductionRequest());

        // Coupe : 500 produits
        productionService.startStep(commande.id(), TypeEtape.COUPE, null);
        OrderResponse apresCoupe = productionService.completeStep(commande.id(), TypeEtape.COUPE,
                quantites(commande, 500));
        assertThat(apresCoupe.quantiteProduiteTotale()).isEqualTo(500);

        // Couture : 480 produits
        productionService.startStep(commande.id(), TypeEtape.COUTURE, null);
        productionService.completeStep(commande.id(), TypeEtape.COUTURE, quantites(commande, 480));

        // Finition : 450 produits
        productionService.startStep(commande.id(), TypeEtape.FINITION, null);
        OrderResponse apresFinition = productionService.completeStep(commande.id(), TypeEtape.FINITION,
                quantites(commande, 450));
        assertThat(apresFinition.statut()).isEqualTo(StatutCommande.EN_CONTROLE);
        assertThat(apresFinition.quantiteProduiteTotale()).isEqualTo(450);

        // Controle qualite conforme
        QualityControlRequest qualite = new QualityControlRequest();
        qualite.setResultat(ResultatControle.CONFORME);
        qualite.setObservations("Aucun defaut majeur");
        OrderResponse controlee = productionService.qualityControl(commande.id(), qualite);
        assertThat(controlee.controleQualite()).isNotNull();

        // Livraison
        DeliveryRequest livraison = new DeliveryRequest();
        livraison.setReceptionnaire("M. Ben Salah");
        livraison.setBonLivraison("BL-001");
        assertThat(productionService.deliver(commande.id(), livraison).quantiteLivree()).isEqualTo(450);

        // Commande non modifiable apres livraison
        UpdateOrderRequest update = new UpdateOrderRequest();
        update.setCommentaire("tentative");
        assertThatThrownBy(() -> commandeService.update(commande.id(), update))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("livree ne peut plus etre modifiee");

        ProductionResponse production = productionService.getProduction(commande.id());
        assertThat(production.etapes()).hasSize(6);
        assertThat(production.etapes().stream().filter(e ->
                "TERMINEE".equals(e.statut().name())).count()).isEqualTo(6);
    }

    @Test
    @DisplayName("Impossible de produire plus que la quantite commandee")
    void quantiteSuperieureCommandee() {
        OrderResponse commande = commandeService.create(creerCommande(500));
        productionService.startProduction(commande.id(), new StartProductionRequest());
        productionService.startStep(commande.id(), TypeEtape.COUPE, null);

        assertThatThrownBy(() -> productionService.completeStep(commande.id(), TypeEtape.COUPE,
                quantites(commande, 501)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("quantite commandee");
    }

    @Test
    @DisplayName("Impossible de produire plus que la quantite de l'etape precedente")
    void quantiteSuperieureEtapePrecedente() {
        OrderResponse commande = commandeService.create(creerCommande(500));
        productionService.startProduction(commande.id(), new StartProductionRequest());
        productionService.startStep(commande.id(), TypeEtape.COUPE, null);
        productionService.completeStep(commande.id(), TypeEtape.COUPE, quantites(commande, 480));

        productionService.startStep(commande.id(), TypeEtape.COUTURE, null);

        // 500 > 480 : la couture ne peut pas remonter le nombre de pieces produites.
        assertThatThrownBy(() -> productionService.completeStep(commande.id(), TypeEtape.COUTURE,
                quantites(commande, 500)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("etape precedente");
    }

    @Test
    @DisplayName("Une commande non conforme ne peut pas etre livree")
    void livraisonBloqueeSiNonConforme() {
        OrderResponse commande = commandeService.create(creerCommande(200));
        productionService.startProduction(commande.id(), new StartProductionRequest());
        productionService.startStep(commande.id(), TypeEtape.COUPE, null);
        productionService.completeStep(commande.id(), TypeEtape.COUPE, quantites(commande, 200));
        productionService.startStep(commande.id(), TypeEtape.COUTURE, null);
        productionService.completeStep(commande.id(), TypeEtape.COUTURE, quantites(commande, 190));
        productionService.startStep(commande.id(), TypeEtape.FINITION, null);
        productionService.completeStep(commande.id(), TypeEtape.FINITION, quantites(commande, 185));

        QualityControlRequest qualite = new QualityControlRequest();
        qualite.setResultat(ResultatControle.NON_CONFORME);
        qualite.setPointsNonConformites("Coutures non conformes sur 12 pieces");
        productionService.qualityControl(commande.id(), qualite);

        DeliveryRequest livraison = new DeliveryRequest();
        livraison.setReceptionnaire("M. Test");
        assertThatThrownBy(() -> productionService.deliver(commande.id(), livraison))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("NON CONFORME");
    }

    @Test
    @DisplayName("Le controle qualite est impossible avant la fin de la finition")
    void controleQualiqueAvantFinition() {
        OrderResponse commande = commandeService.create(creerCommande(100));
        productionService.startProduction(commande.id(), new StartProductionRequest());

        QualityControlRequest qualite = new QualityControlRequest();
        qualite.setResultat(ResultatControle.CONFORME);
        assertThatThrownBy(() -> productionService.qualityControl(commande.id(), qualite))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Finition n'est pas terminee");
    }

    @Test
    @DisplayName("Une commande en retard est correctement signalee")
    void detectionRetard() {
        OrderResponse commande = commandeService.create(creerCommande(50));
        assertThat(commande.enRetard()).isFalse();

        UpdateOrderRequest update = new UpdateOrderRequest();
        update.setCommentaire("relance client");
        commandeService.update(commande.id(), update);

        CreateOrderRequest rapide = creerCommande(50);
        rapide.setDateLivraisonPrevue(LocalDate.now().plusDays(10));
        rapide.setPriorite(PrioriteCommande.TRES_URGENTE);
        OrderResponse urgente = commandeService.create(rapide);
        assertThat(urgente.priorite()).isEqualTo(PrioriteCommande.TRES_URGENTE);

        assertThat(commandeService.listActive()).isNotEmpty();
    }
}