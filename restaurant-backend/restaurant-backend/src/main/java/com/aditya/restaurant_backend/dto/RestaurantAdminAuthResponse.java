package com.aditya.restaurant_backend.dto;

public record RestaurantAdminAuthResponse(

        String token,

        String role,

        Long adminId,

        String adminName,

        String adminEmail,

        Long restaurantId,

        String restaurantName,

        boolean mustChangePassword

) {
}