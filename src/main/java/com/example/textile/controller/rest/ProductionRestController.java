package com.example.textile.controller.rest;

import com.example.textile.dto.request.*;
import com.example.textile.dto.response.DeliveryResponse;
import com.example.textile.dto.response.OrderResponse;
import com.example.textile.dto.response.ProductionResponse;
import com.example.textile.entity.TypeEtape;
import com.example.textile.service.ProductionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API REST du workflow de production.
 */
@RestController
@RequestMapping("/api/orders")
public class ProductionRestController {

    private final ProductionService productionService;

    public ProductionRestController(ProductionService productionService) {
        this.productionService = productionService;
    }

    /** Demarre la production : cree les etapes du workflow. */
    @PostMapping("/{id}/production/start")
    public OrderResponse startProduction(@PathVariable Long id,
                                         @Valid @RequestBody(required = false) StartProductionRequest request) {
        return productionService.startProduction(id, request != null ? request : new StartProductionRequest());
    }

    /** Demarre une etape precise du workflow. */
    @PostMapping("/{id}/production/steps/{step}/start")
    public OrderResponse startStep(@PathVariable Long id,
                                   @PathVariable TypeEtape step,
                                   @RequestParam(required = false) String commentaire) {
        return productionService.startStep(id, step, commentaire);
    }

    /** Termine une etape de production en declarant les quantites produites. */
    @PostMapping("/{id}/production/steps/{step}/complete")
    public OrderResponse completeStep(@PathVariable Long id,
                                      @PathVariable TypeEtape step,
                                      @Valid @RequestBody CompleteStepRequest request) {
        return productionService.completeStep(id, step, request);
    }

    /** Etat detaille du workflow et des quantites. */
    @GetMapping("/{id}/production")
    public ProductionResponse production(@PathVariable Long id) {
        return productionService.getProduction(id);
    }

    /** Controle qualite : CONFORME / NON_CONFORME. */
    @PostMapping("/{id}/quality-control")
    public ResponseEntity<OrderResponse> qualityControl(@PathVariable Long id,
                                                        @Valid @RequestBody QualityControlRequest request) {
        OrderResponse response = productionService.qualityControl(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Livraison : uniquement si controle qualite CONFORME. */
    @PostMapping("/{id}/delivery")
    public ResponseEntity<DeliveryResponse> deliver(@PathVariable Long id,
                                                    @Valid @RequestBody DeliveryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productionService.deliver(id, request));
    }

    /** Annulation de la commande. */
    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id,
                                @RequestParam(required = false) String motif) {
        return productionService.cancel(id, motif);
    }
}