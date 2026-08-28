package com.aditya.restaurant_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.entity.CustomerOrder;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.service.RestaurantAdminOrderService;

@RestController
@RequestMapping("/api/restaurant-admin/orders")
public class RestaurantAdminOrderController {

    private final RestaurantAdminOrderService
            restaurantAdminOrderService;

    public RestaurantAdminOrderController(
            RestaurantAdminOrderService restaurantAdminOrderService
    ) {
        this.restaurantAdminOrderService =
                restaurantAdminOrderService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerOrder>>
            getOrders(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminOrderService
                        .getOrders(
                                restaurantId
                        )
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<CustomerOrder>
            getOrder(
                    @AuthenticationPrincipal Jwt jwt,
                    @PathVariable Long orderId
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminOrderService
                        .getOrder(
                                restaurantId,
                                orderId
                        )
        );
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<CustomerOrder>
            updateOrderStatus(
                    @AuthenticationPrincipal Jwt jwt,
                    @PathVariable Long orderId,
                    @RequestBody Map<String, String> request
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        String statusValue =
                request.get("status");

        OrderStatus status;

        try {
            status =
                    statusValue == null
                            ? null
                            : OrderStatus.valueOf(
                                    statusValue
                                            .trim()
                                            .toUpperCase()
                            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid order status"
            );
        }

        return ResponseEntity.ok(
                restaurantAdminOrderService
                        .updateOrderStatus(
                                restaurantId,
                                orderId,
                                status
                        )
        );
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>>
            getOrderCount(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        long count =
                restaurantAdminOrderService
                        .getOrderCount(
                                restaurantId
                        );

        return ResponseEntity.ok(
                Map.of(
                        "count",
                        count
                )
        );
    }
}