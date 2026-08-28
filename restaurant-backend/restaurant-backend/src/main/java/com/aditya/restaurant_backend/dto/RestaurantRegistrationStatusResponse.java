package com.aditya.restaurant_backend.dto;

import java.time.LocalDateTime;

import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;

public record RestaurantRegistrationStatusResponse(

        Long restaurantId,
        String restaurantName,
        RestaurantApprovalStatus approvalStatus,
        boolean restaurantActive,
        boolean adminActive,
        LocalDateTime submittedAt

) {
}