package com.aditya.restaurant_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.RestaurantAdminAuthResponse;
import com.aditya.restaurant_backend.dto.RestaurantAdminChangePasswordRequest;
import com.aditya.restaurant_backend.dto.RestaurantAdminLoginRequest;
import com.aditya.restaurant_backend.service.RestaurantAdminAuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurant-admin/auth")
public class RestaurantAdminAuthController {

    private final RestaurantAdminAuthService
            restaurantAdminAuthService;

    public RestaurantAdminAuthController(
            RestaurantAdminAuthService restaurantAdminAuthService
    ) {
        this.restaurantAdminAuthService =
                restaurantAdminAuthService;
    }

    // ==========================================
    // LOGIN
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<RestaurantAdminAuthResponse>
            login(
                    @Valid
                    @RequestBody
                    RestaurantAdminLoginRequest request
            ) {

        RestaurantAdminAuthResponse response =
                restaurantAdminAuthService
                        .login(
                                request
                        );

        return ResponseEntity.ok(
                response
        );
    }

    // ==========================================
    // CHANGE PASSWORD
    // ==========================================

    @PostMapping("/change-password")
    public ResponseEntity<Void>
            changePassword(
                    @Valid
                    @RequestBody
                    RestaurantAdminChangePasswordRequest request,

                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        Long adminId =
                jwt.getClaim(
                        "adminId"
                );

        Long restaurantId =
                jwt.getClaim(
                        "restaurantId"
                );

        restaurantAdminAuthService
                .changePassword(
                        adminId,
                        restaurantId,
                        request
                );

        return ResponseEntity
                .noContent()
                .build();
    }
}