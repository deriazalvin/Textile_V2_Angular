package com.example.textile.controller.rest;

import com.example.textile.dto.response.DashboardResponse;
import com.example.textile.dto.response.ProductionStatisticsResponse;
import com.example.textile.service.DashboardService;
import com.example.textile.service.StatistiquesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsRestController {

    private final DashboardService dashboardService;
    private final StatistiquesService statistiquesService;

    public StatsRestController(DashboardService dashboardService,
                               StatistiquesService statistiquesService) {
        this.dashboardService = dashboardService;
        this.statistiquesService = statistiquesService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return dashboardService.getDashboard();
    }

    /** Statistiques de production : volumétrie, rendements, délais, qualité. */
    @GetMapping("/production")
    public ProductionStatisticsResponse production() {
        return statistiquesService.statistiques();
    }
}