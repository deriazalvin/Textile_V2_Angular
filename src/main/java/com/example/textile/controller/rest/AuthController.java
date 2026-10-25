package com.example.textile.controller.rest;

import com.example.textile.entity.AppUser;
import com.example.textile.repository.AppUserRepository;
import com.example.textile.security.AppRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Authentification de l'interface Angular.
 *
 * <p>L'API reste protégée par HTTP Basic : ce contrôleur sert uniquement à
 * exposer l'identité de l'utilisateur courant (rôle pour l'affichage conditionnel
 * du menu) et à permettre la création d'un compte de consultation.</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Identité de l'utilisateur authentifié (en-tête HTTP Basic). */
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        AppUser user = appUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Utilisateur authentifié introuvable : " + authentication.getName()));
        return Map.of(
                "username", user.getUsername(),
                "displayName", user.getDisplayName() == null ? user.getUsername() : user.getDisplayName(),
                "role", user.getRole().name());
    }

    /**
     * Inscription publique : crée un compte de consultation (ROLE_USER).
     * Les comptes à privilèges restent créés par un administrateur.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String username = nettoyer(body.get("username"));
        String password = body.get("password");
        String displayName = nettoyer(body.get("displayName"));

        if (username == null || username.length() < 3 || username.length() > 60) {
            return ResponseEntity.badRequest().body(Map.of("message",
                    "L'identifiant doit contenir entre 3 et 60 caractères"));
        }
        if (password == null || password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message",
                    "Le mot de passe doit contenir au moins 6 caractères"));
        }
        if (appUserRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "L'identifiant " + username + " est déjà utilisé"));
        }

        AppUser saved = appUserRepository.save(new AppUser(
                username,
                passwordEncoder.encode(password),
                displayName == null || displayName.isBlank() ? username : displayName,
                AppRole.ROLE_USER));

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", saved.getId(),
                "username", saved.getUsername(),
                "displayName", saved.getDisplayName(),
                "role", saved.getRole().name()));
    }

    private static String nettoyer(String valeur) {
        return valeur == null ? null : valeur.trim();
    }
}
