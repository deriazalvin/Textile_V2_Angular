package com.example.textile.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * Construction securise d'un Pageable : champs de tri autorises uniquement,
 * taille de page bornee.
 */
public final class PageableUtils {

    private static final int MAX_PAGE_SIZE = 200;
    private static final Set<String> ALLOWED_ORDER_FIELDS = Set.of(
            "numero", "dateCommande", "dateLivraisonPrevue", "priorite", "statut",
            "montantTotal", "id", "name", "code", "designation", "createdAt");

    private PageableUtils() {
    }

    public static Pageable of(int page, int size, String sort) {
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        int safePage = Math.max(page, 0);
        return PageRequest.of(safePage, safeSize, toSort(sort));
    }

    public static Sort toSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "dateCommande");
        }
        Sort result = Sort.unsorted();
        for (String part : sort.split(",")) {
            String[] bits = part.trim().split(":");
            String field = bits[0];
            if (!ALLOWED_ORDER_FIELDS.contains(field)) {
                continue;
            }
            Sort.Direction direction = bits.length > 1 && "asc".equalsIgnoreCase(bits[1])
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            result = result.and(Sort.by(direction, field));
        }
        return result.isUnsorted() ? Sort.by(Sort.Direction.DESC, "dateCommande") : result;
    }
}