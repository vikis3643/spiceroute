package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PlatformSettingResponse(

        Long id,

        String platformName,

        String supportEmail,
        String supportPhone,

        BigDecimal defaultCommissionPercentage,
        BigDecimal defaultDeliveryFee,
        BigDecimal minimumOrderAmount,

        boolean maintenanceMode,
        boolean restaurantRegistrationEnabled,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}