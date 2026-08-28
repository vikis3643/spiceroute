package com.aditya.restaurant_backend.dto;

public record RestaurantAdminProfileResponse(
        Long adminId,
        String adminName,
        String adminEmail,
        Long restaurantId,
        String restaurantName
) {
}