package com.example.textile.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Gestion des erreurs pour l'interface Thymeleaf : messages comprehensibles
 * affiches a l'utilisateur.
 */
@ControllerAdvice(basePackages = "com.example.textile.controller.web")
public class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model, HttpServletRequest request) {
        return message(model, request, "Ressource introuvable", ex.getMessage());
    }

    @ExceptionHandler({BusinessException.class, InvalidOperationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusiness(RuntimeException ex, Model model, HttpServletRequest request) {
        return message(model, request, "Operation impossible", ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleConflict(ConflictException ex, Model model, HttpServletRequest request) {
        return message(model, request, "Conflit", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnexpected(Exception ex, Model model, HttpServletRequest request) {
        log.error("Erreur inattendue sur {} {}", request.getMethod(), request.getRequestURI(), ex);
        return message(model, request, "Erreur interne",
                "Une erreur inattendue s'est produite. Veuillez reessayer ou contacter l'administrateur.");
    }

    private String message(Model model, HttpServletRequest request, String titre, String detail) {
        model.addAttribute("errorTitre", titre);
        model.addAttribute("errorMessage", detail);
        model.addAttribute("errorPath", request.getRequestURI());
        model.addAttribute("activeMenu", deduceMenu(request.getRequestURI()));
        return "erreur";
    }

    private String deduceMenu(String uri) {
        if (uri.startsWith("/clients")) {
            return "clients";
        }
        if (uri.startsWith("/produits")) {
            return "produits";
        }
        if (uri.startsWith("/commandes")) {
            return "commandes";
        }
        if (uri.startsWith("/admin")) {
            return "admin";
        }
        return "dashboard";
    }
}