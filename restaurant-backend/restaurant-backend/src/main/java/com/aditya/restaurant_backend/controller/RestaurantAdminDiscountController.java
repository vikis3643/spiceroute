package com.aditya.restaurant_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.entity.Discount;
import com.aditya.restaurant_backend.service.RestaurantAdminDiscountService;

@RestController
@RequestMapping("/api/restaurant-admin/discounts")
public class RestaurantAdminDiscountController {

    private final RestaurantAdminDiscountService
            restaurantAdminDiscountService;

    public RestaurantAdminDiscountController(
            RestaurantAdminDiscountService restaurantAdminDiscountService
    ) {
        this.restaurantAdminDiscountService =
                restaurantAdminDiscountService;
    }

    @GetMapping
    public ResponseEntity<List<Discount>>
            getDiscounts(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminDiscountService
                        .getDiscounts(
                                restaurantId
                        )
        );
    }

    @GetMapping("/{discountId}")
    public ResponseEntity<Discount>
            getDiscount(
                    @AuthenticationPrincipal Jwt jwt,
                    @PathVariable Long discountId
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminDiscountService
                        .getDiscount(
                                restaurantId,
                                discountId
                        )
        );
    }

    @PostMapping
    public ResponseEntity<Discount>
            createDiscount(
                    @AuthenticationPrincipal Jwt jwt,
                    @RequestBody Discount discount
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        Discount createdDiscount =
                restaurantAdminDiscountService
                        .createDiscount(
                                restaurantId,
                                discount
                        );

        return ResponseEntity.ok(
                createdDiscount
        );
    }

    @PutMapping("/{discountId}")
    public ResponseEntity<Discount>
            updateDiscount(
                    @AuthenticationPrincipal Jwt jwt,
                    @PathVariable Long discountId,
                    @RequestBody Discount discount
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        Discount updatedDiscount =
                restaurantAdminDiscountService
                        .updateDiscount(
                                restaurantId,
                                discountId,
                                discount
                        );

        return ResponseEntity.ok(
                updatedDiscount
        );
    }

    @DeleteMapping("/{discountId}")
    public ResponseEntity<Void>
            deleteDiscount(
                    @AuthenticationPrincipal Jwt jwt,
                    @PathVariable Long discountId
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        restaurantAdminDiscountService
                .deleteDiscount(
                        restaurantId,
                        discountId
                );

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>>
            getDiscountCount(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        long count =
                restaurantAdminDiscountService
                        .getDiscountCount(
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