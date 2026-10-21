package com.example.textile.service;

import com.example.textile.dto.request.CreateProduitRequest;
import com.example.textile.dto.request.UpdateProduitRequest;
import com.example.textile.dto.response.PageResponse;
import com.example.textile.dto.response.ProduitResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProduitService {

    ProduitResponse create(CreateProduitRequest request);

    ProduitResponse update(Long id, UpdateProduitRequest request);

    void delete(Long id);

    ProduitResponse getById(Long id);

    PageResponse<ProduitResponse> search(String search, String categorie, Boolean active, Pageable pageable);

    List<String> listCategories();
}