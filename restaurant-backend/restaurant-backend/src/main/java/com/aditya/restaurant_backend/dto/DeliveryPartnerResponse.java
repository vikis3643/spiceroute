package com.aditya.restaurant_backend.dto;

import java.time.LocalDateTime;

import com.aditya.restaurant_backend.entity.DeliveryPartnerStatus;

public record DeliveryPartnerResponse(

        Long id,
        String fullName,
        String email,
        String phone,
        String vehicleNumber,
        String vehicleType,

        DeliveryPartnerStatus status,
        boolean active,

        long totalAssignments,
        long deliveredAssignments,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}