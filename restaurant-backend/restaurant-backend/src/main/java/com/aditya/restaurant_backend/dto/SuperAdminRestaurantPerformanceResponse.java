package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;

public record SuperAdminRestaurantPerformanceResponse(

        Long restaurantId,
        String restaurantName,

        boolean active,

        BigDecimal commissionPercentage,

        long totalOrders,
        long deliveredOrders,
        long cancelledOrders,

        BigDecimal deliveredRevenue,
        BigDecimal deliveredSubtotal,

        BigDecimal platformCommission,
        BigDecimal restaurantNetEarnings

) {
}