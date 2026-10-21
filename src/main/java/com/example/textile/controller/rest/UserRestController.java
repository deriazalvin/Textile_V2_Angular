package com.example.textile.controller.rest;

import com.example.textile.entity.AppUser;
import com.example.textile.repository.AppUserRepository;
import com.example.textile.security.AppRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Administration des comptes (ROLE_ADMIN). Volontairement hors du package
 * gere par GlobalExceptionHandler pour rester minimal : seules les erreurs
 * HTTP standards sont retournees.
 */
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRestController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public Page<Map<String, Object>> list(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        Page<AppUser> result = appUserRepository.findAll(
                PageRequest.of(Math.max(page, 0), Math.min(size, 200), Sort.by("username")));
        return result.map(u -> Map.<String, Object>of(
                "id", u.getId(),
                "username", u.getUsername(),
                "displayName", u.getDisplayName() == null ? "" : u.getDisplayName(),
                "role", u.getRole(),
                "enabled", u.isEnabled()));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String displayName = body.getOrDefault("displayName", username);
        String role = body.getOrDefault("role", AppRole.ROLE_USER.name());
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message",
                    "Les champs username et password sont obligatoires"));
        }
        if (appUserRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "L'utilisateur " + username + " existe deja"));
        }
        AppRole appRole;
        try {
            appRole = AppRole.valueOf(role);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", "Role inconnu : " + role));
        }
        AppUser saved = appUserRepository.save(
                new AppUser(username, passwordEncoder.encode(password), displayName, appRole));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", saved.getId(),
                "username", saved.getUsername(),
                "displayName", saved.getDisplayName() == null ? "" : saved.getDisplayName(),
                "role", saved.getRole()));
    }

    @PutMapping("/{id}/enabled")
    public ResponseEntity<Map<String, Object>> setEnabled(@PathVariable Long id,
                                                           @RequestParam boolean enabled) {
        return appUserRepository.findById(id)
                .map(user -> {
                    user.setEnabled(enabled);
                    appUserRepository.save(user);
                    return ResponseEntity.ok(Map.<String, Object>of(
                            "id", user.getId(), "enabled", user.isEnabled()));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Utilisateur introuvable : " + id)));
    }
}