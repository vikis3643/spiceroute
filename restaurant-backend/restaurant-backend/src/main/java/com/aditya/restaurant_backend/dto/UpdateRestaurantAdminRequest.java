package com.aditya.restaurant_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRestaurantAdminRequest(

        @NotBlank(
                message = "Full name is required"
        )
        @Size(
                min = 2,
                max = 100,
                message = "Full name must contain 2 to 100 characters"
        )
        String fullName,

        @NotBlank(
                message = "Email is required"
        )
        @Email(
                message = "Enter a valid email"
        )
        String email

) {
}