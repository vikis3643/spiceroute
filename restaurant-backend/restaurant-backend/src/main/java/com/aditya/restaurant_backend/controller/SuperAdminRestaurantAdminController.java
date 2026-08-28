package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.CreateRestaurantAdminRequest;
import com.aditya.restaurant_backend.dto.RestaurantAdminActiveRequest;
import com.aditya.restaurant_backend.dto.RestaurantAdminPasswordResetRequest;
import com.aditya.restaurant_backend.dto.SuperAdminRestaurantAdminResponse;
import com.aditya.restaurant_backend.dto.UpdateRestaurantAdminRequest;
import com.aditya.restaurant_backend.service.SuperAdminRestaurantAdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/super-admin/restaurant-admins")
public class SuperAdminRestaurantAdminController {

    private final SuperAdminRestaurantAdminService
            superAdminRestaurantAdminService;

    public SuperAdminRestaurantAdminController(
            SuperAdminRestaurantAdminService superAdminRestaurantAdminService
    ) {
        this.superAdminRestaurantAdminService =
                superAdminRestaurantAdminService;
    }

    // ==========================================
    // LIST ALL / FILTER BY RESTAURANT
    // ==========================================

    @GetMapping
    public ResponseEntity<
            List<SuperAdminRestaurantAdminResponse>
            > getAdmins(
                    @RequestParam(
                            required = false
                    )
                    Long restaurantId
            ) {

        List<SuperAdminRestaurantAdminResponse>
                admins;

        if (restaurantId == null) {
            admins =
                    superAdminRestaurantAdminService
                            .getAllAdmins();
        } else {
            admins =
                    superAdminRestaurantAdminService
                            .getAdminsByRestaurant(
                                    restaurantId
                            );
        }

        return ResponseEntity.ok(
                admins
        );
    }

    // ==========================================
    // GET ONE ADMIN
    // ==========================================

    @GetMapping("/{adminId}")
    public ResponseEntity<
            SuperAdminRestaurantAdminResponse
            > getAdmin(
                    @PathVariable
                    Long adminId
            ) {

        return ResponseEntity.ok(
                superAdminRestaurantAdminService
                        .getAdmin(
                                adminId
                        )
        );
    }

    // ==========================================
    // CREATE ADMIN
    // ==========================================

    @PostMapping
    public ResponseEntity<
            SuperAdminRestaurantAdminResponse
            > createAdmin(
                    @Valid
                    @RequestBody
                    CreateRestaurantAdminRequest request
            ) {

        return ResponseEntity.ok(
                superAdminRestaurantAdminService
                        .createAdmin(
                                request
                        )
        );
    }

    // ==========================================
    // UPDATE ADMIN DETAILS
    // ==========================================

    @PutMapping("/{adminId}")
    public ResponseEntity<
            SuperAdminRestaurantAdminResponse
            > updateAdmin(
                    @PathVariable
                    Long adminId,

                    @Valid
                    @RequestBody
                    UpdateRestaurantAdminRequest request
            ) {

        return ResponseEntity.ok(
                superAdminRestaurantAdminService
                        .updateAdmin(
                                adminId,
                                request
                        )
        );
    }

    // ==========================================
    // ACTIVATE / DEACTIVATE
    // ==========================================

    @PatchMapping(
            "/{adminId}/active"
    )
    public ResponseEntity<
            SuperAdminRestaurantAdminResponse
            > updateActiveStatus(
                    @PathVariable
                    Long adminId,

                    @RequestBody
                    RestaurantAdminActiveRequest request
            ) {

        return ResponseEntity.ok(
                superAdminRestaurantAdminService
                        .updateActiveStatus(
                                adminId,
                                request
                        )
        );
    }

    // ==========================================
    // RESET PASSWORD
    // ==========================================

    @PatchMapping(
            "/{adminId}/password"
    )
    public ResponseEntity<Void>
            resetPassword(
                    @PathVariable
                    Long adminId,

                    @Valid
                    @RequestBody
                    RestaurantAdminPasswordResetRequest request
            ) {

        superAdminRestaurantAdminService
                .resetPassword(
                        adminId,
                        request
                );

        return ResponseEntity
                .noContent()
                .build();
    }
}