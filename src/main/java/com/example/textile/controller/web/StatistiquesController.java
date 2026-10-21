package com.example.textile.controller.web;

import com.example.textile.dto.response.ProductionStatisticsResponse;
import com.example.textile.service.StatistiquesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Page « Statistiques de production » (exigence §12 de l'énoncé).
 */
@Controller
public class StatistiquesController {

    private final StatistiquesService statistiquesService;

    public StatistiquesController(StatistiquesService statistiquesService) {
        this.statistiquesService = statistiquesService;
    }

    @GetMapping("/statistiques")
    public String statistiques(Model model) {
        ProductionStatisticsResponse stats = statistiquesService.statistiques();
        model.addAttribute("stats", stats);
        model.addAttribute("typesEtape",
                com.example.textile.entity.TypeEtape.values());
        model.addAttribute("activeMenu", "statistiques");
        return "statistiques";
    }
}