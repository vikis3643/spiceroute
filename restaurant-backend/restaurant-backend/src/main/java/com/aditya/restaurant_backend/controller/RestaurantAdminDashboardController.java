package com.aditya.restaurant_backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.service.RestaurantAdminDashboardService;

@RestController
@RequestMapping("/api/restaurant-admin/dashboard")
public class RestaurantAdminDashboardController {

    private final RestaurantAdminDashboardService
            restaurantAdminDashboardService;

    public RestaurantAdminDashboardController(
            RestaurantAdminDashboardService restaurantAdminDashboardService
    ) {
        this.restaurantAdminDashboardService =
                restaurantAdminDashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>>
            getDashboardSummary(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminDashboardService
                        .getDashboardSummary(
                                restaurantId
                        )
        );
    }
}