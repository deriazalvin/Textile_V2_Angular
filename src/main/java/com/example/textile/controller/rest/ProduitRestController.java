package com.example.textile.controller.rest;

import com.example.textile.dto.request.CreateProduitRequest;
import com.example.textile.dto.request.UpdateProduitRequest;
import com.example.textile.dto.response.PageResponse;
import com.example.textile.dto.response.ProduitResponse;
import com.example.textile.service.ProduitService;
import com.example.textile.util.PageableUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitRestController {

    private final ProduitService produitService;

    public ProduitRestController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public PageResponse<ProduitResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "code,asc") String sort) {
        return produitService.search(search, categorie, active, PageableUtils.of(page, size, sort));
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return produitService.listCategories();
    }

    @GetMapping("/{id}")
    public ProduitResponse get(@PathVariable Long id) {
        return produitService.getById(id);
    }

    @PostMapping
    public ResponseEntity<ProduitResponse> create(@Valid @RequestBody CreateProduitRequest request) {
        ProduitResponse created = produitService.create(request);
        return ResponseEntity.created(URI.create("/api/produits/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public ProduitResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProduitRequest request) {
        return produitService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produitService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}