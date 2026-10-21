package com.example.textile.dto.response;

import java.time.LocalDateTime;

public record ClientResponse(
        Long id,
        String name,
        String phone,
        String email,
        String address,
        String city,
        String country,
        String notes,
        boolean active,
        LocalDateTime createdAt,
        long nombreCommandes) {

    public String activeBadgeClass() {
        return active ? "badge--success" : "badge--muted";
    }

    public String activeLabel() {
        return active ? "Actif" : "Inactif";
    }
}