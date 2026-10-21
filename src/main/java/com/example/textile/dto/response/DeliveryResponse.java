package com.example.textile.dto.response;

import java.time.LocalDate;

public record DeliveryResponse(
        Long id,
        String numero,
        LocalDate dateLivraison,
        String receptionnaire,
        String bonLivraison,
        String commentaire,
        Integer quantiteLivree,
        Long retardJours) {
}