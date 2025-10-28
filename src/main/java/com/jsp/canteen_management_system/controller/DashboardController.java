package com.jsp.canteen_management_system.controller;

import com.jsp.canteen_management_system.dto.DashboardStats;
import com.jsp.canteen_management_system.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardStats getStats() {
        return dashboardService.getDashboardStats();
    }
}
