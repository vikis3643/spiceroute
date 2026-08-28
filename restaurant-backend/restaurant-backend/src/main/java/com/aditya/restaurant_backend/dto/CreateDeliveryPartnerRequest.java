package com.aditya.restaurant_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDeliveryPartnerRequest(

        @NotBlank(message = "Full name is required")
        @Size(
                min = 2,
                max = 100,
                message = "Full name must contain 2 to 100 characters"
        )
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email")
        String email,

        @NotBlank(message = "Phone is required")
        @Size(
                min = 10,
                max = 15,
                message = "Phone must contain 10 to 15 characters"
        )
        String phone,

        String vehicleNumber,

        String vehicleType

) {
}