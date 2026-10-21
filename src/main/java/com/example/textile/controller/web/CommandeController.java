package com.example.textile.controller.web;

import com.example.textile.dto.request.CreateOrderRequest;
import com.example.textile.dto.request.OrderForm;
import com.example.textile.dto.request.OrderLineRequest;
import com.example.textile.dto.request.UpdateOrderRequest;
import com.example.textile.dto.response.OrderResponse;
import com.example.textile.dto.response.PageResponse;
import com.example.textile.entity.PrioriteCommande;
import com.example.textile.entity.StatutCommande;
import com.example.textile.service.ClientService;
import com.example.textile.service.CommandeService;
import com.example.textile.service.ProduitService;
import com.example.textile.service.ProductionService;
import com.example.textile.util.PageableUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/commandes")
public class CommandeController {

    private final CommandeService commandeService;
    private final ClientService clientService;
    private final ProduitService produitService;
    private final ProductionService productionService;

    public CommandeController(CommandeService commandeService,
                              ClientService clientService,
                              ProduitService produitService,
                              ProductionService productionService) {
        this.commandeService = commandeService;
        this.clientService = clientService;
        this.produitService = produitService;
        this.productionService = productionService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) StatutCommande statut,
                       @RequestParam(required = false) PrioriteCommande priorite,
                       @RequestParam(required = false) Boolean delayed,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "dateCommande,desc") String sort,
                       Model model) {
        PageResponse<OrderResponse> result = commandeService.search(search, statut, priorite, null,
                Boolean.TRUE.equals(delayed), PageableUtils.of(page, size, sort));
        model.addAttribute("pageResponse", result);
        model.addAttribute("listUrl", "/commandes");
        model.addAttribute("commandes", result);
        model.addAttribute("statuts", StatutCommande.values());
        model.addAttribute("priorites", PrioriteCommande.values());
        model.addAttribute("search", search);
        model.addAttribute("statut", statut);
        model.addAttribute("priorite", priorite);
        model.addAttribute("delayed", delayed);
        model.addAttribute("activeMenu", "commandes");
        return "commandes/liste";
    }

    @GetMapping("/retard")
    public String retard(Model model) {
        List<OrderResponse> commandes = commandeService.listDelayed();
        model.addAttribute("commandesRetard", commandes);
        model.addAttribute("activeMenu", "commandes");
        return "commandes/retard";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        OrderResponse commande = commandeService.getById(id);
        model.addAttribute("commande", commande);
        model.addAttribute("activeMenu", "commandes");
        return "commandes/detail";
    }

    @GetMapping("/nouvelle")
    public String createForm(Model model) {
        OrderForm form = new OrderForm();
        form.addLigne();
        model.addAttribute("form", form);
        model.addAttribute("clients", clientService.search(null, true,
                PageableUtils.of(0, 200, "name,asc")).content());
        model.addAttribute("produits", produitService.search(null, null, true,
                PageableUtils.of(0, 200, "code,asc")).content());
        model.addAttribute("priorites", PrioriteCommande.values());
        model.addAttribute("formAction", "/commandes");
        model.addAttribute("formTitle", "Nouvelle commande");
        model.addAttribute("activeMenu", "commandes");
        return "commandes/formulaire";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") OrderForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (form.getLignes().isEmpty()) {
            bindingResult.reject("lignes", "La commande doit contenir au moins une ligne");
        }
        if (bindingResult.hasErrors()) {
            remplirReferences(model);
            model.addAttribute("formAction", "/commandes");
            model.addAttribute("formTitle", "Nouvelle commande");
            model.addAttribute("activeMenu", "commandes");
            return "commandes/formulaire";
        }
        try {
            OrderResponse created = commandeService.create(toCreateRequest(form));
            redirectAttributes.addFlashAttribute("successMessage",
                    "Commande " + created.numero() + " creee avec succes");
            return "redirect:/commandes/" + created.id();
        } catch (RuntimeException ex) {
            bindingResult.reject("global", ex.getMessage());
            remplirReferences(model);
            model.addAttribute("formAction", "/commandes");
            model.addAttribute("formTitle", "Nouvelle commande");
            model.addAttribute("activeMenu", "commandes");
            return "commandes/formulaire";
        }
    }

    @GetMapping("/{id}/modifier")
    public String editForm(@PathVariable Long id, Model model) {
        OrderResponse commande = commandeService.getById(id);
        if (commande.statut() == StatutCommande.LIVREE || commande.statut() == StatutCommande.ANNULEE) {
            model.addAttribute("errorTitre", "Modification impossible");
            model.addAttribute("errorMessage",
                    "La commande " + commande.numero() + " est terminee et ne peut plus etre modifiee.");
            model.addAttribute("errorPath", "/commandes/" + id);
            model.addAttribute("activeMenu", "commandes");
            return "erreur";
        }
        OrderForm form = new OrderForm();
        form.setId(commande.id());
        form.setClientId(commande.clientId());
        form.setDateLivraisonPrevue(commande.dateLivraisonPrevue());
        form.setPriorite(commande.priorite());
        form.setCommentaire(commande.commentaire());
        form.setInstructionsSpeciales(commande.instructionsSpeciales());
        commande.lignes().forEach(l -> {
            OrderForm.LigneForm ligne = new OrderForm.LigneForm();
            ligne.setId(l.id());
            ligne.setProduitId(l.produitId());
            ligne.setQuantite(l.quantite());
            ligne.setTaille(l.taille());
            ligne.setCouleur(l.couleur());
            ligne.setPrixUnitaire(l.prixUnitaire());
            form.getLignes().add(ligne);
        });
        model.addAttribute("form", form);
        remplirReferences(model);
        model.addAttribute("formAction", "/commandes/" + id);
        model.addAttribute("formTitle", "Modifier la commande " + commande.numero());
        model.addAttribute("activeMenu", "commandes");
        return "commandes/formulaire";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") OrderForm form,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            remplirReferences(model);
            model.addAttribute("formAction", "/commandes/" + id);
            model.addAttribute("formTitle", "Modifier la commande");
            model.addAttribute("activeMenu", "commandes");
            return "commandes/formulaire";
        }
        try {
            UpdateOrderRequest request = new UpdateOrderRequest();
            request.setClientId(form.getClientId());
            request.setDateLivraisonPrevue(form.getDateLivraisonPrevue());
            request.setPriorite(form.getPriorite());
            request.setCommentaire(form.getCommentaire());
            request.setInstructionsSpeciales(form.getInstructionsSpeciales());
            if (commandeService.getById(id).statut() == StatutCommande.BROUILLON
                    || commandeService.getById(id).statut() == StatutCommande.PLANIFIEE) {
                request.setLignes(form.getLignes().stream().map(this::toLineRequest).toList());
            }
            commandeService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Commande mise a jour");
            return "redirect:/commandes/" + id;
        } catch (RuntimeException ex) {
            bindingResult.reject("global", ex.getMessage());
            remplirReferences(model);
            model.addAttribute("formAction", "/commandes/" + id);
            model.addAttribute("formTitle", "Modifier la commande");
            model.addAttribute("activeMenu", "commandes");
            return "commandes/formulaire";
        }
    }

    @PostMapping("/{id}/supprimer")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            commandeService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Commande supprimee");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/commandes";
    }

    @PostMapping("/{id}/annuler")
    public String cancel(@PathVariable Long id,
                         @RequestParam(required = false) String motif,
                         RedirectAttributes redirectAttributes) {
        try {
            OrderResponse annulee = productionService.cancel(id, motif);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Commande " + annulee.numero() + " annulee");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/commandes/" + id;
    }


    private void remplirReferences(Model model) {
        model.addAttribute("clients", clientService.search(null, true,
                PageableUtils.of(0, 200, "name,asc")).content());
        model.addAttribute("produits", produitService.search(null, null, true,
                PageableUtils.of(0, 200, "code,asc")).content());
        model.addAttribute("priorites", PrioriteCommande.values());
    }

    private CreateOrderRequest toCreateRequest(OrderForm form) {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setClientId(form.getClientId());
        request.setDateLivraisonPrevue(form.getDateLivraisonPrevue());
        request.setPriorite(form.getPriorite());
        request.setCommentaire(form.getCommentaire());
        request.setInstructionsSpeciales(form.getInstructionsSpeciales());
        request.setLignes(form.getLignes().stream().map(this::toLineRequest).toList());
        return request;
    }

    private OrderLineRequest toLineRequest(OrderForm.LigneForm ligne) {
        OrderLineRequest request = new OrderLineRequest();
        request.setProduitId(ligne.getProduitId());
        request.setQuantite(ligne.getQuantite());
        request.setTaille(ligne.getTaille());
        request.setCouleur(ligne.getCouleur());
        request.setPrixUnitaire(ligne.getPrixUnitaire());
        return request;
    }
}