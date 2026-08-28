package com.aditya.restaurant_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.PlatformSettingResponse;
import com.aditya.restaurant_backend.dto.UpdatePlatformSettingRequest;
import com.aditya.restaurant_backend.service.PlatformSettingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/super-admin/settings")
public class SuperAdminPlatformSettingController {

    private final PlatformSettingService
            platformSettingService;

    public SuperAdminPlatformSettingController(
            PlatformSettingService platformSettingService
    ) {

        this.platformSettingService =
                platformSettingService;
    }

    // ==========================================
    // GET PLATFORM SETTINGS
    // ==========================================

    @GetMapping
    public ResponseEntity<PlatformSettingResponse>
            getSettings() {

        return ResponseEntity.ok(
                platformSettingService
                        .getSettings()
        );
    }

    // ==========================================
    // UPDATE PLATFORM SETTINGS
    // ==========================================

    @PutMapping
    public ResponseEntity<PlatformSettingResponse>
            updateSettings(
                    @Valid
                    @RequestBody
                    UpdatePlatformSettingRequest request
            ) {

        return ResponseEntity.ok(
                platformSettingService
                        .updateSettings(
                                request
                        )
        );
    }
}