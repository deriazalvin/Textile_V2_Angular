package com.example.textile.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Generation du numero de commande : CMD-2026-0001
 */
public final class OrderNumberGenerator {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("yyyy");

    private OrderNumberGenerator() {
    }

    public static String next(String prefix, long sequence) {
        return "%s-%s-%04d".formatted(prefix, LocalDate.now().format(FORMAT_DATE), sequence);
    }

    public static String defaultPrefix() {
        return "CMD";
    }
}