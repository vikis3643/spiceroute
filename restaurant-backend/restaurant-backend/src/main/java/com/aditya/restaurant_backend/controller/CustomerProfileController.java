package com.aditya.restaurant_backend.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.CustomerProfileRequest;
import com.aditya.restaurant_backend.dto.CustomerProfileResponse;
import com.aditya.restaurant_backend.service.CustomerProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer/profile")
public class CustomerProfileController {

    private final CustomerProfileService
            customerProfileService;

    public CustomerProfileController(
            CustomerProfileService
                    customerProfileService
    ) {
        this.customerProfileService =
                customerProfileService;
    }

    @GetMapping
    public CustomerProfileResponse getProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return customerProfileService.getProfile(
                jwt.getSubject()
        );
    }

    @PutMapping
    public CustomerProfileResponse updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid
            @RequestBody
            CustomerProfileRequest request
    ) {
        return customerProfileService
                .updateProfile(
                        jwt.getSubject(),
                        request
                );
    }
}