package com.aditya.restaurant_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestaurantRegistrationRequest(

        @NotBlank(message = "Restaurant name is required")
        @Size(
                min = 2,
                max = 150,
                message = "Restaurant name must contain 2 to 150 characters"
        )
        String restaurantName,

        @Size(
                max = 1000,
                message = "Description cannot exceed 1000 characters"
        )
        String description,

        @NotBlank(message = "Restaurant email is required")
        @Email(message = "Restaurant email is invalid")
        String restaurantEmail,

        @NotBlank(message = "Restaurant phone is required")
        @Size(
                min = 10,
                max = 15,
                message = "Restaurant phone must contain 10 to 15 characters"
        )
        String restaurantPhone,

        @NotBlank(message = "Restaurant address is required")
        @Size(
                max = 1000,
                message = "Address cannot exceed 1000 characters"
        )
        String address,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        String logoUrl,

        @NotBlank(message = "Owner name is required")
        @Size(
                min = 2,
                max = 150,
                message = "Owner name must contain 2 to 150 characters"
        )
        String ownerName,

        @NotBlank(message = "Owner email is required")
        @Email(message = "Owner email is invalid")
        String ownerEmail

) {
}