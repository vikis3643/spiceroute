package com.aditya.restaurant_backend.dto;

import com.aditya.restaurant_backend.entity.DeliveryPartnerStatus;

import jakarta.validation.constraints.NotNull;

public record DeliveryPartnerStatusRequest(

        @NotNull(message = "Delivery partner status is required")
        DeliveryPartnerStatus status

) {
}