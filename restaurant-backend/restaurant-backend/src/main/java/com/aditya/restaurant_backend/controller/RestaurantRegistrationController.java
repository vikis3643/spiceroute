package com.aditya.restaurant_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.RestaurantRegistrationRequest;
import com.aditya.restaurant_backend.dto.RestaurantRegistrationResponse;
import com.aditya.restaurant_backend.dto.RestaurantRegistrationStatusResponse;
import com.aditya.restaurant_backend.service.RestaurantRegistrationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurant-registration")
public class RestaurantRegistrationController {

    private final RestaurantRegistrationService
            restaurantRegistrationService;

    public RestaurantRegistrationController(
            RestaurantRegistrationService restaurantRegistrationService
    ) {

        this.restaurantRegistrationService =
                restaurantRegistrationService;
    }

    // ==========================================
    // REGISTER NEW RESTAURANT
    // ==========================================

    @PostMapping
    public ResponseEntity<RestaurantRegistrationResponse>
            registerRestaurant(
                    @Valid
                    @RequestBody
                    RestaurantRegistrationRequest request
            ) {

        RestaurantRegistrationResponse response =
                restaurantRegistrationService
                        .register(
                                request
                        );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        response
                );
    }

    // ==========================================
    // CHECK REGISTRATION STATUS
    // ==========================================

    @GetMapping("/status")
    public ResponseEntity<RestaurantRegistrationStatusResponse>
            getRegistrationStatus(
                    @RequestParam
                    String email
            ) {

        return ResponseEntity.ok(
                restaurantRegistrationService
                        .getRegistrationStatus(
                                email
                        )
        );
    }
}