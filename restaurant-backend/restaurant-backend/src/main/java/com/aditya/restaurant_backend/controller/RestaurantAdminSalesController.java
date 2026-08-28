package com.aditya.restaurant_backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.service.RestaurantAdminSalesService;

@RestController
@RequestMapping("/api/restaurant-admin/sales")
public class RestaurantAdminSalesController {

    private final RestaurantAdminSalesService
            restaurantAdminSalesService;

    public RestaurantAdminSalesController(
            RestaurantAdminSalesService restaurantAdminSalesService
    ) {
        this.restaurantAdminSalesService =
                restaurantAdminSalesService;
    }

    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>>
            getTodaySales(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminSalesService
                        .getTodaySales(
                                restaurantId
                        )
        );
    }

    @GetMapping("/range")
    public ResponseEntity<Map<String, Object>>
            getSalesByDateRange(
                    @AuthenticationPrincipal Jwt jwt,

                    @RequestParam
                    @DateTimeFormat(
                            iso = DateTimeFormat.ISO.DATE
                    )
                    LocalDate startDate,

                    @RequestParam
                    @DateTimeFormat(
                            iso = DateTimeFormat.ISO.DATE
                    )
                    LocalDate endDate
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminSalesService
                        .getSalesByDateRange(
                                restaurantId,
                                startDate,
                                endDate
                        )
        );
    }

    @GetMapping("/orders")
    public ResponseEntity<List<CustomerOrder>>
            getOrdersByDateRange(
                    @AuthenticationPrincipal Jwt jwt,

                    @RequestParam
                    @DateTimeFormat(
                            iso = DateTimeFormat.ISO.DATE
                    )
                    LocalDate startDate,

                    @RequestParam
                    @DateTimeFormat(
                            iso = DateTimeFormat.ISO.DATE
                    )
                    LocalDate endDate
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminSalesService
                        .getOrdersByDateRange(
                                restaurantId,
                                startDate,
                                endDate
                        )
        );
    }
}