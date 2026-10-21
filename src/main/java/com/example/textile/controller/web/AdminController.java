package com.example.textile.controller.web;

import com.example.textile.entity.AppUser;
import com.example.textile.repository.AppUserRepository;
import com.example.textile.security.AppRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/utilisateurs")
    public String list(Model model) {
        List<AppUser> users = appUserRepository.findAll();
        model.addAttribute("utilisateurs", users);
        model.addAttribute("roles", AppRole.values());
        model.addAttribute("activeMenu", "admin");
        return "admin/utilisateurs";
    }

    @PostMapping("/utilisateurs")
    public String create(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam(required = false) String displayName,
                         @RequestParam AppRole role,
                         RedirectAttributes redirectAttributes) {
        if (appUserRepository.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "L'utilisateur " + username + " existe deja");
            return "redirect:/admin/utilisateurs";
        }
        String nom = (displayName == null || displayName.isBlank()) ? username : displayName;
        appUserRepository.save(new AppUser(username, passwordEncoder.encode(password), nom, role));
        redirectAttributes.addFlashAttribute("successMessage",
                "Utilisateur " + username + " cree avec le role " + role);
        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/utilisateurs/{id}/actif")
    public String toggleEnabled(@PathVariable Long id,
                                @RequestParam boolean enabled,
                                RedirectAttributes redirectAttributes) {
        appUserRepository.findById(id).ifPresentOrElse(user -> {
            user.setEnabled(enabled);
            appUserRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Utilisateur " + user.getUsername() + (enabled ? " active" : " desactive"));
        }, () -> redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur introuvable"));
        return "redirect:/admin/utilisateurs";
    }
}