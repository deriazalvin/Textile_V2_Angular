package com.example.textile.exception;

/**
 * Regle de gestion violee -> HTTP 400.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}