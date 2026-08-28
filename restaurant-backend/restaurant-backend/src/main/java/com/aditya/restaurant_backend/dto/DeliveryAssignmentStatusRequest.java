package com.aditya.restaurant_backend.dto;

import com.aditya.restaurant_backend.entity.DeliveryAssignmentStatus;

import jakarta.validation.constraints.NotNull;

public record DeliveryAssignmentStatusRequest(

        @NotNull(message = "Assignment status is required")
        DeliveryAssignmentStatus status

) {
}