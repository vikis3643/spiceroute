package com.aditya.restaurant_backend.dto;

public record RestaurantAdminLoginRequest(
        String email,
        String password
) {
}