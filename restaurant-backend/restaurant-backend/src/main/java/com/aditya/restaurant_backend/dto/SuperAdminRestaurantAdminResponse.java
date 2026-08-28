package com.aditya.restaurant_backend.dto;

import java.time.LocalDateTime;

public record SuperAdminRestaurantAdminResponse(

        Long id,
        Long restaurantId,
        String restaurantName,

        String fullName,
        String email,

        boolean active,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}