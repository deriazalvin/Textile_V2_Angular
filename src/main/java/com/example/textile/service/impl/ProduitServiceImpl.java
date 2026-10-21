package com.example.textile.service.impl;

import com.example.textile.dto.request.CreateProduitRequest;
import com.example.textile.dto.request.UpdateProduitRequest;
import com.example.textile.dto.response.PageResponse;
import com.example.textile.dto.response.ProduitResponse;
import com.example.textile.entity.Commande;
import com.example.textile.entity.Produit;
import com.example.textile.exception.BusinessException;
import com.example.textile.exception.ConflictException;
import com.example.textile.exception.ResourceNotFoundException;
import com.example.textile.mapper.ProduitMapper;
import com.example.textile.repository.CommandeRepository;
import com.example.textile.repository.ProduitRepository;
import com.example.textile.service.ProduitService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;
    private final ProduitMapper produitMapper;

    public ProduitServiceImpl(ProduitRepository produitRepository,
                              CommandeRepository commandeRepository,
                              ProduitMapper produitMapper) {
        this.produitRepository = produitRepository;
        this.commandeRepository = commandeRepository;
        this.produitMapper = produitMapper;
    }

    @Override
    public ProduitResponse create(CreateProduitRequest request) {
        if (produitRepository.existsByCode(request.getCode())) {
            throw new ConflictException("Un produit avec le code " + request.getCode() + " existe deja");
        }
        return produitMapper.toResponse(produitRepository.save(produitMapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public ProduitResponse getById(Long id) {
        return produitMapper.toResponse(find(id));
    }

    @Override
    public ProduitResponse update(Long id, UpdateProduitRequest request) {
        return produitMapper.toResponse(produitRepository.save(updateEntity(find(id), request)));
    }

    private Produit updateEntity(Produit produit, UpdateProduitRequest request) {
        produitMapper.updateEntity(produit, request);
        return produit;
    }

    @Override
    public void delete(Long id) {
        Produit produit = find(id);
        if (commandeRepository.existsByProduitId(id)) {
            throw new BusinessException("Impossible de supprimer le produit " + produit.getCode()
                    + " : il est utilise dans des commandes. Desactivez-le plutot.");
        }
        produitRepository.delete(produit);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProduitResponse> search(String search, String categorie, Boolean active,
                                                Pageable pageable) {
        return PageResponse.from(
                produitRepository.search(normalize(search), normalize(categorie), active, pageable),
                produitMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listCategories() {
        return produitRepository.findAll().stream()
                .map(Produit::getCategorie)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .sorted()
                .toList();
    }

    private Produit find(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Produit", id));
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}