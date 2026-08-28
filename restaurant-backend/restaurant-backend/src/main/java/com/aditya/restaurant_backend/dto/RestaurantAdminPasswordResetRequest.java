package com.aditya.restaurant_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestaurantAdminPasswordResetRequest(

        @NotBlank(
                message = "New password is required"
        )
        @Size(
                min = 8,
                max = 100,
                message = "Password must contain at least 8 characters"
        )
        String newPassword

) {
}