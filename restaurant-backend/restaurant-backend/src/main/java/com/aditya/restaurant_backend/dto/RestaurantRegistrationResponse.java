package com.aditya.restaurant_backend.dto;

import java.time.LocalDateTime;

import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;

public record RestaurantRegistrationResponse(

        Long restaurantId,
        String restaurantName,
        String restaurantEmail,

        RestaurantApprovalStatus approvalStatus,
        boolean restaurantActive,

        Long adminId,
        String ownerName,
        String ownerEmail,
        boolean adminActive,

        LocalDateTime submittedAt,

        String message

) {
}