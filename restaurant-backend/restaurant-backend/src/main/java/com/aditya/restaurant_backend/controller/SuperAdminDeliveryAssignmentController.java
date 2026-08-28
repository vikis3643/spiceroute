package com.aditya.restaurant_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aditya.restaurant_backend.dto.CreateDeliveryAssignmentRequest;
import com.aditya.restaurant_backend.dto.DeliveryAssignmentResponse;
import com.aditya.restaurant_backend.dto.DeliveryAssignmentStatusRequest;
import com.aditya.restaurant_backend.service.SuperAdminDeliveryAssignmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/super-admin/delivery-assignments")
public class SuperAdminDeliveryAssignmentController {

    private final SuperAdminDeliveryAssignmentService
            deliveryAssignmentService;

    public SuperAdminDeliveryAssignmentController(
            SuperAdminDeliveryAssignmentService deliveryAssignmentService
    ) {

        this.deliveryAssignmentService =
                deliveryAssignmentService;
    }

    // ==========================================
    // LIST ALL ASSIGNMENTS
    // ==========================================

    @GetMapping
    public ResponseEntity<List<DeliveryAssignmentResponse>>
            getAllAssignments() {

        return ResponseEntity.ok(
                deliveryAssignmentService
                        .getAllAssignments()
        );
    }

    // ==========================================
    // GET ONE ASSIGNMENT
    // ==========================================

    @GetMapping("/{assignmentId}")
    public ResponseEntity<DeliveryAssignmentResponse>
            getAssignment(
                    @PathVariable
                    Long assignmentId
            ) {

        return ResponseEntity.ok(
                deliveryAssignmentService
                        .getAssignment(
                                assignmentId
                        )
        );
    }

    // ==========================================
    // GET ASSIGNMENT BY ORDER
    // ==========================================

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryAssignmentResponse>
            getAssignmentByOrder(
                    @PathVariable
                    Long orderId
            ) {

        return ResponseEntity.ok(
                deliveryAssignmentService
                        .getAssignmentByOrder(
                                orderId
                        )
        );
    }

    // ==========================================
    // CREATE ASSIGNMENT
    // ==========================================

    @PostMapping
    public ResponseEntity<DeliveryAssignmentResponse>
            createAssignment(
                    @Valid
                    @RequestBody
                    CreateDeliveryAssignmentRequest request
            ) {

        DeliveryAssignmentResponse response =
                deliveryAssignmentService
                        .createAssignment(
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
    // UPDATE ASSIGNMENT STATUS
    // ==========================================

    @PatchMapping("/{assignmentId}/status")
    public ResponseEntity<DeliveryAssignmentResponse>
            updateStatus(
                    @PathVariable
                    Long assignmentId,

                    @Valid
                    @RequestBody
                    DeliveryAssignmentStatusRequest request
            ) {

        return ResponseEntity.ok(
                deliveryAssignmentService
                        .updateStatus(
                                assignmentId,
                                request
                        )
        );
    }
}