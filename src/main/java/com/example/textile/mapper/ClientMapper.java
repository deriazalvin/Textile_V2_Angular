package com.example.textile.mapper;

import com.example.textile.dto.request.CreateClientRequest;
import com.example.textile.dto.request.UpdateClientRequest;
import com.example.textile.dto.response.ClientResponse;
import com.example.textile.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toEntity(CreateClientRequest request) {
        Client client = new Client(request.getName(), request.getPhone(), request.getEmail(),
                request.getAddress(), request.getCity());
        if (request.getNotes() != null) {
            client.setNotes(request.getNotes());
        }
        return client;
    }

    public void updateEntity(Client client, UpdateClientRequest request) {
        if (request.getName() != null && !request.getName().isBlank()) {
            client.setName(request.getName());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            client.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            client.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            client.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            client.setCity(request.getCity());
        }
        if (request.getNotes() != null) {
            client.setNotes(request.getNotes());
        }
        if (request.getActive() != null) {
            client.setActive(request.getActive());
        }
    }

    public ClientResponse toResponse(Client client) {
        return toResponse(client, 0);
    }

    public ClientResponse toResponse(Client client, long nombreCommandes) {
        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getPhone(),
                client.getEmail(),
                client.getAddress(),
                client.getCity(),
                client.getCountry(),
                client.getNotes(),
                client.isActive(),
                client.getCreatedAt(),
                nombreCommandes);
    }
}