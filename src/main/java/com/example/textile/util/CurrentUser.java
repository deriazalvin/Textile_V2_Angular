package com.example.textile.util;

import com.example.textile.entity.AppUser;
import com.example.textile.exception.ResourceNotFoundException;
import com.example.textile.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resolution de l'utilisateur connecte pour l'attribuer aux etapes / controles.
 */
@Component
public class CurrentUser {

    private final AppUserRepository appUserRepository;

    public CurrentUser(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser currentOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null
                || "anonymousUser".equals(auth.getName())) {
            return null;
        }
        return appUserRepository.findByUsername(auth.getName()).orElse(null);
    }

    /** Utilisateur connecte ; leve une erreur si l'authentification est absente. */
    public AppUser requireCurrent() {
        AppUser user = currentOrNull();
        if (user == null) {
            throw ResourceNotFoundException.of("Aucun utilisateur authentifie");
        }
        return user;
    }

    public String currentUsername() {
        AppUser user = currentOrNull();
        return user != null ? user.getUsername() : "system";
    }
}