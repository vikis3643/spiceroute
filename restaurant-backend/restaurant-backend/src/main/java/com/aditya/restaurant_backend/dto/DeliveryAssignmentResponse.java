package com.aditya.restaurant_backend.dto;

import java.time.LocalDateTime;

import com.aditya.restaurant_backend.entity.DeliveryAssignmentStatus;

public record DeliveryAssignmentResponse(

        Long id,

        Long orderId,

        Long restaurantId,
        String restaurantName,

        String customerName,
        String customerPhone,
        String deliveryAddress,

        Long deliveryPartnerId,
        String deliveryPartnerName,
        String deliveryPartnerPhone,

        DeliveryAssignmentStatus status,

        LocalDateTime assignedAt,
        LocalDateTime acceptedAt,
        LocalDateTime pickedUpAt,
        LocalDateTime deliveredAt,
        LocalDateTime cancelledAt,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}