package com.aditya.restaurant_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.SuperAdminLoginRequest;
import com.aditya.restaurant_backend.dto.SuperAdminLoginResponse;
import com.aditya.restaurant_backend.service.SuperAdminAuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/super-admin/auth")
public class SuperAdminAuthController {

    private final SuperAdminAuthService
            superAdminAuthService;

    public SuperAdminAuthController(
            SuperAdminAuthService superAdminAuthService
    ) {
        this.superAdminAuthService =
                superAdminAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<SuperAdminLoginResponse>
            login(
                    @Valid
                    @RequestBody
                    SuperAdminLoginRequest request
            ) {

        SuperAdminLoginResponse response =
                superAdminAuthService.login(
                        request
                );

        return ResponseEntity.ok(
                response
        );
    }
}