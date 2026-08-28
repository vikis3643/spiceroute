package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.aditya.restaurant_backend.dto.CreateDeliveryPartnerRequest;
import com.aditya.restaurant_backend.dto.DeliveryPartnerActiveRequest;
import com.aditya.restaurant_backend.dto.DeliveryPartnerResponse;
import com.aditya.restaurant_backend.dto.DeliveryPartnerStatusRequest;
import com.aditya.restaurant_backend.dto.UpdateDeliveryPartnerRequest;
import com.aditya.restaurant_backend.entity.DeliveryPartnerStatus;
import com.aditya.restaurant_backend.service.SuperAdminDeliveryPartnerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/super-admin/delivery-partners")
public class SuperAdminDeliveryPartnerController {

    private final SuperAdminDeliveryPartnerService
            deliveryPartnerService;

    public SuperAdminDeliveryPartnerController(
            SuperAdminDeliveryPartnerService deliveryPartnerService
    ) {
        this.deliveryPartnerService =
                deliveryPartnerService;
    }

    // ==========================================
    // GET ALL / FILTER BY STATUS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<DeliveryPartnerResponse>>
            getPartners(
                    @RequestParam(required = false)
                    DeliveryPartnerStatus status
            ) {

        if (status != null) {

            return ResponseEntity.ok(
                    deliveryPartnerService
                            .getPartnersByStatus(
                                    status
                            )
            );
        }

        return ResponseEntity.ok(
                deliveryPartnerService
                        .getAllPartners()
        );
    }

    // ==========================================
    // GET AVAILABLE PARTNERS
    // ==========================================

    @GetMapping("/available")
    public ResponseEntity<List<DeliveryPartnerResponse>>
            getAvailablePartners() {

        return ResponseEntity.ok(
                deliveryPartnerService
                        .getAvailablePartners()
        );
    }

    // ==========================================
    // GET ONE PARTNER
    // ==========================================

    @GetMapping("/{partnerId}")
    public ResponseEntity<DeliveryPartnerResponse>
            getPartner(
                    @PathVariable
                    Long partnerId
            ) {

        return ResponseEntity.ok(
                deliveryPartnerService
                        .getPartner(
                                partnerId
                        )
        );
    }

    // ==========================================
    // CREATE PARTNER
    // ==========================================

    @PostMapping
    public ResponseEntity<DeliveryPartnerResponse>
            createPartner(
                    @Valid
                    @RequestBody
                    CreateDeliveryPartnerRequest request
            ) {

        DeliveryPartnerResponse response =
                deliveryPartnerService
                        .createPartner(
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
    // UPDATE PARTNER DETAILS
    // ==========================================

    @PutMapping("/{partnerId}")
    public ResponseEntity<DeliveryPartnerResponse>
            updatePartner(
                    @PathVariable
                    Long partnerId,

                    @Valid
                    @RequestBody
                    UpdateDeliveryPartnerRequest request
            ) {

        return ResponseEntity.ok(
                deliveryPartnerService
                        .updatePartner(
                                partnerId,
                                request
                        )
        );
    }

    // ==========================================
    // ACTIVATE / DEACTIVATE
    // ==========================================

    @PatchMapping("/{partnerId}/active")
    public ResponseEntity<DeliveryPartnerResponse>
            updateActiveStatus(
                    @PathVariable
                    Long partnerId,

                    @RequestBody
                    DeliveryPartnerActiveRequest request
            ) {

        return ResponseEntity.ok(
                deliveryPartnerService
                        .updateActiveStatus(
                                partnerId,
                                request
                        )
        );
    }

    // ==========================================
    // UPDATE WORK STATUS
    // ==========================================

    @PatchMapping("/{partnerId}/status")
    public ResponseEntity<DeliveryPartnerResponse>
            updateStatus(
                    @PathVariable
                    Long partnerId,

                    @Valid
                    @RequestBody
                    DeliveryPartnerStatusRequest request
            ) {

        return ResponseEntity.ok(
                deliveryPartnerService
                        .updateStatus(
                                partnerId,
                                request
                        )
        );
    }
}