package com.example.textile.dto.response;

import java.time.LocalDateTime;
import java.util.Map;


public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, null);
    }

    public static ErrorResponse of(int status, String error, String message, String path,
                                   Map<String, String> fieldErrors) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path,
                fieldErrors == null || fieldErrors.isEmpty() ? null : fieldErrors);
    }
}