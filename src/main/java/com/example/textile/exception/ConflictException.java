package com.example.textile.exception;

/**
 * Conflit d'unicite / etat concurrent -> HTTP 409.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}