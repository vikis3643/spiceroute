package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record SuperAdminCommissionRequest(

        @NotNull(
                message = "Commission percentage is required"
        )
        @DecimalMin(
                value = "0.00",
                message = "Commission cannot be negative"
        )
        @DecimalMax(
                value = "100.00",
                message = "Commission cannot exceed 100"
        )
        BigDecimal commissionPercentage

) {
}