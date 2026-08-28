package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePlatformSettingRequest(

        @NotBlank(message = "Platform name is required")
        @Size(
                min = 2,
                max = 100,
                message = "Platform name must contain 2 to 100 characters"
        )
        String platformName,

        @Email(message = "Enter a valid support email")
        String supportEmail,

        @Size(
                max = 20,
                message = "Support phone cannot exceed 20 characters"
        )
        String supportPhone,

        @NotNull(message = "Default commission is required")
        @DecimalMin(
                value = "0.00",
                message = "Commission cannot be negative"
        )
        @DecimalMax(
                value = "100.00",
                message = "Commission cannot exceed 100%"
        )
        BigDecimal defaultCommissionPercentage,

        @NotNull(message = "Default delivery fee is required")
        @DecimalMin(
                value = "0.00",
                message = "Delivery fee cannot be negative"
        )
        BigDecimal defaultDeliveryFee,

        @NotNull(message = "Minimum order amount is required")
        @DecimalMin(
                value = "0.00",
                message = "Minimum order amount cannot be negative"
        )
        BigDecimal minimumOrderAmount,

        boolean maintenanceMode,

        boolean restaurantRegistrationEnabled

) {
}