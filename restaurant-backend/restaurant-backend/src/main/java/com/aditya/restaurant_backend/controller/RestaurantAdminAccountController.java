package com.aditya.restaurant_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.RestaurantAdminAccountResponse;
import com.aditya.restaurant_backend.dto.RestaurantAdminChangePasswordRequest;
import com.aditya.restaurant_backend.dto.UpdateRestaurantAdminAccountRequest;
import com.aditya.restaurant_backend.service.RestaurantAdminAccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurant-admin/account")
public class RestaurantAdminAccountController {

    private final RestaurantAdminAccountService
            restaurantAdminAccountService;

    public RestaurantAdminAccountController(
            RestaurantAdminAccountService restaurantAdminAccountService
    ) {
        this.restaurantAdminAccountService =
                restaurantAdminAccountService;
    }

    // ==========================================
    // GET OWN ACCOUNT
    // ==========================================

    @GetMapping
    public ResponseEntity<RestaurantAdminAccountResponse>
            getAccount(
                    @AuthenticationPrincipal
                    Jwt jwt
            ) {

        Long adminId =
                getAdminId(
                        jwt
                );

        return ResponseEntity.ok(
                restaurantAdminAccountService
                        .getAccount(
                                adminId
                        )
        );
    }

    // ==========================================
    // UPDATE OWN ACCOUNT
    // ==========================================

    @PutMapping
    public ResponseEntity<RestaurantAdminAccountResponse>
            updateAccount(
                    @AuthenticationPrincipal
                    Jwt jwt,

                    @Valid
                    @RequestBody
                    UpdateRestaurantAdminAccountRequest request
            ) {

        Long adminId =
                getAdminId(
                        jwt
                );

        return ResponseEntity.ok(
                restaurantAdminAccountService
                        .updateAccount(
                                adminId,
                                request
                        )
        );
    }

    // ==========================================
    // CHANGE PASSWORD
    // ==========================================

    @PatchMapping("/password")
    public ResponseEntity<Void>
            changePassword(
                    @AuthenticationPrincipal
                    Jwt jwt,

                    @Valid
                    @RequestBody
                    RestaurantAdminChangePasswordRequest request
            ) {

        Long adminId =
                getAdminId(
                        jwt
                );

        restaurantAdminAccountService
                .changePassword(
                        adminId,
                        request
                );

        return ResponseEntity
                .noContent()
                .build();
    }

    // ==========================================
    // JWT ADMIN ID
    // ==========================================

    private Long getAdminId(
            Jwt jwt
    ) {

        Number adminId =
                jwt.getClaim(
                        "adminId"
                );

        return adminId.longValue();
    }
}