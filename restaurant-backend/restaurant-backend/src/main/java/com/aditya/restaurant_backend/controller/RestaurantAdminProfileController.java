package com.aditya.restaurant_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.RestaurantProfileUpdateRequest;
import com.aditya.restaurant_backend.entity.Restaurant;
import com.aditya.restaurant_backend.service.RestaurantAdminProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurant-admin/profile")
public class RestaurantAdminProfileController {

    private final RestaurantAdminProfileService
            restaurantAdminProfileService;

    public RestaurantAdminProfileController(
            RestaurantAdminProfileService restaurantAdminProfileService
    ) {
        this.restaurantAdminProfileService =
                restaurantAdminProfileService;
    }

    @GetMapping
    public ResponseEntity<Restaurant>
            getProfile(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminProfileService
                        .getProfile(
                                restaurantId
                        )
        );
    }

    @PutMapping
    public ResponseEntity<Restaurant>
            updateProfile(
                    @AuthenticationPrincipal Jwt jwt,
                    @Valid
                    @RequestBody
                    RestaurantProfileUpdateRequest request
            ) {

        Long restaurantId =
                jwt.getClaim("restaurantId");

        return ResponseEntity.ok(
                restaurantAdminProfileService
                        .updateProfile(
                                restaurantId,
                                request
                        )
        );
    }
}