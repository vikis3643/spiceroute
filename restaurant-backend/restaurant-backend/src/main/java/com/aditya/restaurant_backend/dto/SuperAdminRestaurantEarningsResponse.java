package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;

public record SuperAdminRestaurantEarningsResponse(

        Long restaurantId,
        String restaurantName,

        BigDecimal commissionPercentage,

        long deliveredOrders,

        BigDecimal deliveredSubtotal,

        BigDecimal platformCommission,

        BigDecimal restaurantNetEarnings

) {
}