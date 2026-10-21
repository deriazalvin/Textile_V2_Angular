package com.example.textile.controller.web;

import com.example.textile.dto.request.CreateProduitRequest;
import com.example.textile.dto.request.UpdateProduitRequest;
import com.example.textile.dto.response.PageResponse;
import com.example.textile.dto.response.ProduitResponse;
import com.example.textile.service.ProduitService;
import com.example.textile.util.PageableUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produits")
public class ProduitController {

    private final ProduitService produitService;

    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) String categorie,
                       @RequestParam(required = false) Boolean active,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "code,asc") String sort,
                       Model model) {
        PageResponse<ProduitResponse> result = produitService.search(search, categorie, active,
                PageableUtils.of(page, size, sort));
        model.addAttribute("pageResponse", result);
        model.addAttribute("listUrl", "/produits");
        model.addAttribute("produits", result);
        model.addAttribute("categories", produitService.listCategories());
        model.addAttribute("search", search);
        model.addAttribute("categorie", categorie);
        model.addAttribute("active", active);
        model.addAttribute("activeMenu", "produits");
        return "produits/liste";
    }

    @GetMapping("/nouveau")
    public String createForm(Model model) {
        model.addAttribute("produit", new CreateProduitRequest());
        model.addAttribute("categories", produitService.listCategories());
        model.addAttribute("formAction", "/produits");
        model.addAttribute("formTitle", "Nouveau produit textile");
            model.addAttribute("edition", false);
        model.addAttribute("activeMenu", "produits");
        return "produits/formulaire";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("produit") CreateProduitRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", produitService.listCategories());
            model.addAttribute("formAction", "/produits");
            model.addAttribute("formTitle", "Nouveau produit textile");
            model.addAttribute("edition", false);
            model.addAttribute("activeMenu", "produits");
            return "produits/formulaire";
        }
        try {
            ProduitResponse created = produitService.create(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Produit " + created.code() + " cree avec succes");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/produits";
    }

    @GetMapping("/{id}/modifier")
    public String editForm(@PathVariable Long id, Model model) {
        ProduitResponse produit = produitService.getById(id);
        UpdateProduitRequest request = new UpdateProduitRequest();
        request.setDesignation(produit.designation());
        request.setCategorie(produit.categorie());
        request.setMatiere(produit.matiere());
        request.setPrixUnitaire(produit.prixUnitaire());
        request.setStock(produit.stock());
        request.setActive(produit.active());
        model.addAttribute("produit", request);
        model.addAttribute("produitId", id);
        model.addAttribute("code", produit.code());
        model.addAttribute("formAction", "/produits/" + id);
        model.addAttribute("formTitle", "Modifier le produit");
            model.addAttribute("edition", true);
        model.addAttribute("activeMenu", "produits");
        return "produits/formulaire";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("produit") UpdateProduitRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("produitId", id);
            model.addAttribute("categories", produitService.listCategories());
            model.addAttribute("formAction", "/produits/" + id);
            model.addAttribute("formTitle", "Modifier le produit");
            model.addAttribute("edition", true);
            model.addAttribute("activeMenu", "produits");
            return "produits/formulaire";
        }
        try {
            produitService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Produit mis a jour");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/produits";
    }

    @PostMapping("/{id}/supprimer")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Produit supprime");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/produits";
    }
}