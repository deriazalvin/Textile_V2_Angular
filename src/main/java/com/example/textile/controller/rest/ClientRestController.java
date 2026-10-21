package com.example.textile.controller.rest;

import com.example.textile.dto.request.CreateClientRequest;
import com.example.textile.dto.request.UpdateClientRequest;
import com.example.textile.dto.response.ClientResponse;
import com.example.textile.dto.response.PageResponse;
import com.example.textile.service.ClientService;
import com.example.textile.util.PageableUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/clients")
public class ClientRestController {

    private final ClientService clientService;

    public ClientRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public PageResponse<ClientResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        return clientService.search(search, active, PageableUtils.of(page, size, sort));
    }

    @GetMapping("/{id}")
    public ClientResponse get(@PathVariable Long id) {
        return clientService.getById(id);
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody CreateClientRequest request) {
        ClientResponse created = clientService.create(request);
        return ResponseEntity.created(URI.create("/api/clients/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ClientResponse update(@PathVariable Long id, @Valid @RequestBody UpdateClientRequest request) {
        return clientService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}