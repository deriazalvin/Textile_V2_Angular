package com.example.textile.service;

import com.example.textile.dto.request.CompleteStepRequest;
import com.example.textile.dto.request.DeliveryRequest;
import com.example.textile.dto.request.QualityControlRequest;
import com.example.textile.dto.request.StartProductionRequest;
import com.example.textile.dto.response.DeliveryResponse;
import com.example.textile.dto.response.OrderResponse;
import com.example.textile.dto.response.ProductionResponse;
import com.example.textile.entity.TypeEtape;

//Workflow prod
public interface ProductionService {

    //demar
    OrderResponse startProduction(Long commandeId, StartProductionRequest request);

    //demar étp
    OrderResponse startStep(Long commandeId, TypeEtape type, String commentaire);

    //clot et decl
    OrderResponse completeStep(Long commandeId, TypeEtape type, CompleteStepRequest request);

    ProductionResponse getProduction(Long commandeId);

    //qlt
    OrderResponse qualityControl(Long commandeId, QualityControlRequest request);

    //lir
    DeliveryResponse deliver(Long commandeId, DeliveryRequest request);

    //annul
    OrderResponse cancel(Long commandeId, String motif);
}