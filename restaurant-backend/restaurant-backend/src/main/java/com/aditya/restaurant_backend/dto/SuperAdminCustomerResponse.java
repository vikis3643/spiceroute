package com.aditya.restaurant_backend.dto;

import java.time.LocalDateTime;

public record SuperAdminCustomerResponse(

        Long id,
        String fullName,
        String email,
        String phone,
        String defaultDeliveryAddress,

        String provider,
        boolean emailVerified,
        boolean active,

        long totalOrders,

        LocalDateTime createdAt

) {
}