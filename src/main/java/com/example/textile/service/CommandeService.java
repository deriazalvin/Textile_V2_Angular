package com.example.textile.service;

import com.example.textile.dto.request.CreateOrderRequest;
import com.example.textile.dto.request.UpdateOrderRequest;
import com.example.textile.dto.response.OrderResponse;
import com.example.textile.dto.response.PageResponse;
import com.example.textile.entity.PrioriteCommande;
import com.example.textile.entity.StatutCommande;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommandeService {

    OrderResponse create(CreateOrderRequest request);

    OrderResponse update(Long id, UpdateOrderRequest request);

    void delete(Long id);

    OrderResponse getById(Long id);

    OrderResponse getByNumero(String numero);

    PageResponse<OrderResponse> search(String search, StatutCommande statut, PrioriteCommande priorite,
                                       Long clientId, boolean delayedOnly, Pageable pageable);

    List<OrderResponse> listDelayed();

    List<OrderResponse> listActive();
}