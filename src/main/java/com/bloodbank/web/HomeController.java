package com.bloodbank.web;

import com.bloodbank.domain.BloodGroup;
import com.bloodbank.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final DashboardService dashboardService;

    public HomeController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("stats", dashboardService.stats());
        model.addAttribute("bloodGroups", BloodGroup.values());
        return "index";
    }
}