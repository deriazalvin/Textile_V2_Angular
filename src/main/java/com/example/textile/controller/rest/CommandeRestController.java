package com.example.textile.controller.rest;

import com.example.textile.dto.request.CreateOrderRequest;
import com.example.textile.dto.request.UpdateOrderRequest;
import com.example.textile.dto.response.OrderResponse;
import com.example.textile.dto.response.PageResponse;
import com.example.textile.entity.PrioriteCommande;
import com.example.textile.entity.StatutCommande;
import com.example.textile.service.CommandeService;
import com.example.textile.util.PageableUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class CommandeRestController {

    private final CommandeService commandeService;

    public CommandeRestController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @GetMapping
    public PageResponse<OrderResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) StatutCommande statut,
            @RequestParam(required = false) PrioriteCommande priorite,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false, defaultValue = "false") boolean delayed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateCommande,desc") String sort) {
        return commandeService.search(search, statut, priorite, clientId, delayed,
                PageableUtils.of(page, size, sort));
    }

    /** Commandes susceptibles d'etre livrees en retard. */
    @GetMapping("/delayed")
    public List<OrderResponse> delayed() {
        return commandeService.listDelayed();
    }

    /** Commandes actives triees par priorite puis urgence. */
    @GetMapping("/active")
    public List<OrderResponse> active() {
        return commandeService.listActive();
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return commandeService.getById(id);
    }

    @GetMapping("/numero/{numero}")
    public OrderResponse getByNumero(@PathVariable String numero) {
        return commandeService.getByNumero(numero);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse created = commandeService.create(request);
        return ResponseEntity.created(URI.create("/api/orders/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public OrderResponse update(@PathVariable Long id, @Valid @RequestBody UpdateOrderRequest request) {
        return commandeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commandeService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}