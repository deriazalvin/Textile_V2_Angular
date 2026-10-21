package com.example.textile.service;

import com.example.textile.dto.request.CreateClientRequest;
import com.example.textile.dto.request.UpdateClientRequest;
import com.example.textile.dto.response.ClientResponse;
import com.example.textile.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    ClientResponse create(CreateClientRequest request);

    ClientResponse update(Long id, UpdateClientRequest request);

    void delete(Long id);

    ClientResponse getById(Long id);

    PageResponse<ClientResponse> search(String search, Boolean active, Pageable pageable);
}