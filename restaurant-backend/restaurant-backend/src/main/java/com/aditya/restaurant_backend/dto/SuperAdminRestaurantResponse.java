package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;

public record SuperAdminRestaurantResponse(

        Long id,
        String name,
        String description,
        String email,
        String phone,
        String address,
        String city,
        String state,
        String logoUrl,

        RestaurantApprovalStatus approvalStatus,
        boolean active,
        BigDecimal commissionPercentage,

        long adminCount,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}