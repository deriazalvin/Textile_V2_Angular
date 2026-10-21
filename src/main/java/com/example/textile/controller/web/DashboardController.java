package com.example.textile.controller.web;

import com.example.textile.dto.response.DashboardResponse;
import com.example.textile.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardResponse dashboard = dashboardService.getDashboard();
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("activeMenu", "dashboard");
        return "dashboard";
    }
}