package com.example.textile.controller.web;

import com.example.textile.dto.request.CreateClientRequest;
import com.example.textile.dto.request.UpdateClientRequest;
import com.example.textile.dto.response.ClientResponse;
import com.example.textile.dto.response.PageResponse;
import com.example.textile.service.ClientService;
import com.example.textile.util.PageableUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String search,
                       @RequestParam(required = false) Boolean active,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "name,asc") String sort,
                       Model model) {
        PageResponse<ClientResponse> result = clientService.search(search, active,
                PageableUtils.of(page, size, sort));
        model.addAttribute("pageResponse", result);
        model.addAttribute("listUrl", "/clients");
        model.addAttribute("clients", result);
        model.addAttribute("search", search);
        model.addAttribute("active", active);
        model.addAttribute("activeMenu", "clients");
        return "clients/liste";
    }

    @GetMapping("/nouveau")
    public String createForm(Model model) {
        model.addAttribute("client", new CreateClientRequest());
        model.addAttribute("formAction", "/clients");
        model.addAttribute("formTitle", "Nouveau client");
            model.addAttribute("edition", false);
        model.addAttribute("activeMenu", "clients");
        return "clients/formulaire";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("client") CreateClientRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/clients");
            model.addAttribute("formTitle", "Nouveau client");
            model.addAttribute("edition", false);
            model.addAttribute("activeMenu", "clients");
            return "clients/formulaire";
        }
        try {
            ClientResponse created = clientService.create(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Client " + created.name() + " cree avec succes");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/clients";
    }

    @GetMapping("/{id}/modifier")
    public String editForm(@PathVariable Long id, Model model) {
        ClientResponse client = clientService.getById(id);
        UpdateClientRequest request = new UpdateClientRequest();
        request.setName(client.name());
        request.setPhone(client.phone());
        request.setEmail(client.email());
        request.setAddress(client.address());
        request.setCity(client.city());
        request.setNotes(client.notes());
        request.setActive(client.active());
        model.addAttribute("client", request);
        model.addAttribute("clientId", id);
        model.addAttribute("titre", "Modifier le client");
        model.addAttribute("formAction", "/clients/" + id);
        model.addAttribute("formTitle", "Modifier le client");
            model.addAttribute("edition", true);
        model.addAttribute("activeMenu", "clients");
        return "clients/formulaire";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("client") UpdateClientRequest request,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("clientId", id);
            model.addAttribute("formAction", "/clients/" + id);
            model.addAttribute("formTitle", "Modifier le client");
            model.addAttribute("edition", true);
            model.addAttribute("activeMenu", "clients");
            return "clients/formulaire";
        }
        try {
            ClientResponse updated = clientService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Client " + updated.name() + " mis a jour");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/clients";
    }

    @PostMapping("/{id}/supprimer")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clientService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Client supprime");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/clients";
    }
}