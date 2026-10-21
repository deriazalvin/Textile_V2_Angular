package com.example.textile.exception;

/**
 * Ressource inexistante -> HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String ressource, Object id) {
        return new ResourceNotFoundException(ressource + " introuvable : " + id);
    }

    public static ResourceNotFoundException of(String message) {
        return new ResourceNotFoundException(message);
    }
}