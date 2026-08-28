package com.aditya.restaurant_backend.dto;

import jakarta.validation.constraints.NotNull;

public record CreateDeliveryAssignmentRequest(

        @NotNull(message = "Order is required")
        Long orderId,

        @NotNull(message = "Delivery partner is required")
        Long deliveryPartnerId

) {
}