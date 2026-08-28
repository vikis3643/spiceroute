package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.SuperAdminCommissionRequest;
import com.aditya.restaurant_backend.dto.SuperAdminRestaurantActiveRequest;
import com.aditya.restaurant_backend.dto.SuperAdminRestaurantResponse;
import com.aditya.restaurant_backend.dto.SuperAdminRestaurantStatusRequest;
import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;
import com.aditya.restaurant_backend.service.SuperAdminRestaurantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/super-admin/restaurants")
public class SuperAdminRestaurantController {

    private final SuperAdminRestaurantService
            superAdminRestaurantService;

    public SuperAdminRestaurantController(
            SuperAdminRestaurantService superAdminRestaurantService
    ) {
        this.superAdminRestaurantService =
                superAdminRestaurantService;
    }

    // ==========================================
    // LIST / FILTER RESTAURANTS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<SuperAdminRestaurantResponse>>
            getRestaurants(
                    @RequestParam(
                            required = false
                    )
                    RestaurantApprovalStatus status
            ) {

        List<SuperAdminRestaurantResponse> restaurants;

        if (status == null) {
            restaurants =
                    superAdminRestaurantService
                            .getAllRestaurants();
        } else {
            restaurants =
                    superAdminRestaurantService
                            .getRestaurantsByStatus(
                                    status
                            );
        }

        return ResponseEntity.ok(
                restaurants
        );
    }

    // ==========================================
    // GET ONE RESTAURANT
    // ==========================================

    @GetMapping("/{restaurantId}")
    public ResponseEntity<SuperAdminRestaurantResponse>
            getRestaurant(
                    @PathVariable
                    Long restaurantId
            ) {

        return ResponseEntity.ok(
                superAdminRestaurantService
                        .getRestaurant(
                                restaurantId
                        )
        );
    }

    // ==========================================
    // APPROVE / REJECT / PENDING
    // ==========================================

    @PatchMapping(
            "/{restaurantId}/approval"
    )
    public ResponseEntity<SuperAdminRestaurantResponse>
            updateApprovalStatus(
                    @PathVariable
                    Long restaurantId,

                    @Valid
                    @RequestBody
                    SuperAdminRestaurantStatusRequest request
            ) {

        return ResponseEntity.ok(
                superAdminRestaurantService
                        .updateApprovalStatus(
                                restaurantId,
                                request
                        )
        );
    }

    // ==========================================
    // ACTIVATE / DEACTIVATE
    // ==========================================

    @PatchMapping(
            "/{restaurantId}/active"
    )
    public ResponseEntity<SuperAdminRestaurantResponse>
            updateActiveStatus(
                    @PathVariable
                    Long restaurantId,

                    @RequestBody
                    SuperAdminRestaurantActiveRequest request
            ) {

        return ResponseEntity.ok(
                superAdminRestaurantService
                        .updateActiveStatus(
                                restaurantId,
                                request
                        )
        );
    }

    // ==========================================
    // UPDATE COMMISSION
    // ==========================================

    @PatchMapping(
            "/{restaurantId}/commission"
    )
    public ResponseEntity<SuperAdminRestaurantResponse>
            updateCommission(
                    @PathVariable
                    Long restaurantId,

                    @Valid
                    @RequestBody
                    SuperAdminCommissionRequest request
            ) {

        return ResponseEntity.ok(
                superAdminRestaurantService
                        .updateCommission(
                                restaurantId,
                                request
                        )
        );
    }
}