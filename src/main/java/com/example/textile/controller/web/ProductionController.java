package com.example.textile.controller.web;

import com.example.textile.dto.request.CompleteStepRequest;
import com.example.textile.dto.request.DeliveryRequest;
import com.example.textile.dto.request.QualityControlRequest;
import com.example.textile.dto.request.StartProductionRequest;
import com.example.textile.dto.request.StepCompletionForm;
import com.example.textile.dto.response.OrderResponse;
import com.example.textile.dto.response.ProductionResponse;
import com.example.textile.entity.ResultatControle;
import com.example.textile.entity.TypeEtape;
import com.example.textile.service.CommandeService;
import com.example.textile.service.ProductionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/commandes")
public class ProductionController {

    private final ProductionService productionService;
    private final CommandeService commandeService;

    public ProductionController(ProductionService productionService, CommandeService commandeService) {
        this.productionService = productionService;
        this.commandeService = commandeService;
    }

    // ------------------------------------------------------- workflow visuel

    @GetMapping("/{id}/production")
    public String production(@PathVariable Long id, Model model) {
        ProductionResponse production = productionService.getProduction(id);
        model.addAttribute("production", production);
        model.addAttribute("commande", commandeService.getById(id));
        model.addAttribute("etapes", TypeEtape.values());
        model.addAttribute("activeMenu", "commandes");
        return "commandes/production";
    }

    @PostMapping("/{id}/production/demarrer")
    public String demarrerProduction(@PathVariable Long id,
                                     @RequestParam(required = false) String commentaire,
                                     RedirectAttributes redirectAttributes) {
        StartProductionRequest request = new StartProductionRequest();
        request.setCommentaire(commentaire);
        try {
            OrderResponse commande = productionService.startProduction(id, request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Production de la commande " + commande.numero() + " demarree");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/commandes/" + id + "/production";
    }

    @PostMapping("/{id}/production/etapes/{etape}/demarrer")
    public String demarrerEtape(@PathVariable Long id,
                                @PathVariable TypeEtape etape,
                                @RequestParam(required = false) String commentaire,
                                RedirectAttributes redirectAttributes) {
        try {
            productionService.startStep(id, etape, commentaire);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Etape " + etape.getLibelle() + " demarree");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/commandes/" + id + "/production";
    }

    @GetMapping("/{id}/production/etapes/{etape}/cloturer")
    public String cloturerForm(@PathVariable Long id, @PathVariable TypeEtape etape, Model model) {
        OrderResponse commande = getOrder(id);
        ProductionResponse production = productionService.getProduction(id);

        StepCompletionForm form = new StepCompletionForm();
        form.setEtape(etape.name());
        production.lignes().forEach(l -> {
            StepCompletionForm.QuantiteLigneForm ligne = new StepCompletionForm.QuantiteLigneForm();
            ligne.setLigneId(l.id());
            ligne.setLibelle(l.produitCode() + " - " + l.produitDesignation()
                    + (l.taille() != null ? " / taille " + l.taille() : "")
                    + (l.couleur() != null ? " / " + l.couleur() : ""));
            ligne.setQuantite(l.quantiteProduite());
            ligne.setQuantiteDisponible(l.quantiteProduite());
            form.getQuantites().add(ligne);
        });

        model.addAttribute("form", form);
        model.addAttribute("commande", commande);
        model.addAttribute("production", production);
        model.addAttribute("etape", etape);
        model.addAttribute("activeMenu", "commandes");
        return "commandes/cloturer-etape";
    }

    @PostMapping("/{id}/production/etapes/{etape}/cloturer")
    public String cloturer(@PathVariable Long id,
                           @PathVariable TypeEtape etape,
                           @Valid @ModelAttribute("form") StepCompletionForm form,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            OrderResponse commande = getOrder(id);
            model.addAttribute("commande", commande);
            model.addAttribute("production", productionService.getProduction(id));
            model.addAttribute("etape", etape);
            model.addAttribute("activeMenu", "commandes");
            return "commandes/cloturer-etape";
        }
        CompleteStepRequest request = new CompleteStepRequest();
        request.setCommentaire(form.getCommentaire());
        request.setQuantites(form.getQuantites().stream().map(q -> {
            CompleteStepRequest.StepLineQuantity item = new CompleteStepRequest.StepLineQuantity();
            item.setLigneId(q.getLigneId());
            item.setQuantite(q.getQuantite());
            return item;
        }).toList());
        try {
            OrderResponse commande = productionService.completeStep(id, etape, request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Etape " + etape.getLibelle() + " terminee pour la commande " + commande.numero());
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/commandes/" + id + "/production";
    }

    // ------------------------------------------------------ controle qualite

    @GetMapping("/{id}/qualite")
    public String qualiteForm(@PathVariable Long id, Model model) {
        model.addAttribute("commande", getOrder(id));
        model.addAttribute("resultats", ResultatControle.values());
        model.addAttribute("activeMenu", "commandes");
        return "commandes/qualite";
    }

    @PostMapping("/{id}/qualite")
    public String qualite(@PathVariable Long id,
                          @RequestParam ResultatControle resultat,
                          @RequestParam(required = false) String observations,
                          @RequestParam(required = false) String pointsNonConformites,
                          RedirectAttributes redirectAttributes) {
        QualityControlRequest request = new QualityControlRequest();
        request.setResultat(resultat);
        request.setObservations(observations);
        request.setPointsNonConformites(pointsNonConformites);
        try {
            productionService.qualityControl(id, request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Controle qualite enregistre : " + resultat);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/commandes/" + id + "/production";
    }

    // ------------------------------------------------------------ livraison

    @GetMapping("/{id}/livraison")
    public String livraisonForm(@PathVariable Long id, Model model) {
        OrderResponse commande = getOrder(id);
        if (commande.controleQualite() == null) {
            model.addAttribute("errorTitre", "Livraison impossible");
            model.addAttribute("errorMessage",
                    "Le controle qualite est obligatoire avant toute livraison.");
            model.addAttribute("errorPath", "/commandes/" + id);
            model.addAttribute("activeMenu", "commandes");
            return "erreur";
        }
        if (!"CONFORME".equals(commande.controleQualite().resultat().name())) {
            model.addAttribute("errorTitre", "Livraison impossible");
            model.addAttribute("errorMessage",
                    "La commande est NON CONFORME : elle ne peut pas etre livree.");
            model.addAttribute("errorPath", "/commandes/" + id);
            model.addAttribute("activeMenu", "commandes");
            return "erreur";
        }
        model.addAttribute("commande", commande);
        model.addAttribute("activeMenu", "commandes");
        return "commandes/livraison";
    }

    @PostMapping("/{id}/livraison")
    public String livraison(@PathVariable Long id,
                            @RequestParam String receptionnaire,
                            @RequestParam(required = false) String bonLivraison,
                            @RequestParam(required = false) String commentaire,
                            RedirectAttributes redirectAttributes) {
        DeliveryRequest request = new DeliveryRequest();
        request.setReceptionnaire(receptionnaire);
        request.setBonLivraison(bonLivraison);
        request.setCommentaire(commentaire);
        try {
            productionService.deliver(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Commande livree avec succes");
            return "redirect:/commandes/" + id;
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/commandes/" + id + "/production";
        }
    }

    // ---------------------------------------------------------------- helpers

    private OrderResponse getOrder(Long id) {
        return commandeService.getById(id);
    }
}