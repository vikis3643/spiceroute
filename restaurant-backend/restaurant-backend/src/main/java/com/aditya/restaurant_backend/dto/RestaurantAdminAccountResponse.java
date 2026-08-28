package com.aditya.restaurant_backend.dto;

import java.time.LocalDateTime;

public record RestaurantAdminAccountResponse(

        Long adminId,
        String fullName,
        String email,
        boolean active,

        Long restaurantId,
        String restaurantName,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}