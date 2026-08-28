package com.aditya.restaurant_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.aditya.restaurant_backend.dto.RestaurantAdminProfileResponse;
import com.aditya.restaurant_backend.entity.RestaurantAdmin;
import com.aditya.restaurant_backend.repository.RestaurantAdminRepository;

@RestController
@RequestMapping("/api/restaurant-admin")
public class RestaurantAdminController {

    private final RestaurantAdminRepository
            restaurantAdminRepository;

    public RestaurantAdminController(
            RestaurantAdminRepository restaurantAdminRepository
    ) {
        this.restaurantAdminRepository =
                restaurantAdminRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<RestaurantAdminProfileResponse>
            getCurrentAdmin(
                    @AuthenticationPrincipal Jwt jwt
            ) {

        Long adminId =
                jwt.getClaim("adminId");

        Long restaurantId =
                jwt.getClaim("restaurantId");

        if (adminId == null
                || restaurantId == null) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Invalid Restaurant Admin token"
            );
        }

        RestaurantAdmin admin =
                restaurantAdminRepository
                        .findById(adminId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Restaurant Admin not found"
                                )
                        );

        if (!admin.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Restaurant Admin account is inactive"
            );
        }

        if (admin.getRestaurant() == null
                || !admin.getRestaurant()
                        .getId()
                        .equals(restaurantId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Restaurant access mismatch"
            );
        }

        RestaurantAdminProfileResponse response =
                new RestaurantAdminProfileResponse(
                        admin.getId(),
                        admin.getFullName(),
                        admin.getEmail(),
                        admin.getRestaurant().getId(),
                        admin.getRestaurant().getName()
                );

        return ResponseEntity.ok(response);
    }
}