package com.aditya.restaurant_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.SuperAdminDashboardSummary;
import com.aditya.restaurant_backend.service.SuperAdminDashboardService;

@RestController
@RequestMapping("/api/super-admin/dashboard")
public class SuperAdminDashboardController {

    private final SuperAdminDashboardService
            superAdminDashboardService;

    public SuperAdminDashboardController(
            SuperAdminDashboardService superAdminDashboardService
    ) {
        this.superAdminDashboardService =
                superAdminDashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<SuperAdminDashboardSummary>
            getSummary() {

        SuperAdminDashboardSummary summary =
                superAdminDashboardService
                        .getSummary();

        return ResponseEntity.ok(
                summary
        );
    }
}