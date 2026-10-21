package com.example.textile.exception;

/**
 * Operation non autorisee dans l'etat courant du workflow -> HTTP 400.
 */
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}