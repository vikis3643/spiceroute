package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record SuperAdminPlatformEarningsResponse(

        long totalDeliveredOrders,

        BigDecimal totalDeliveredSubtotal,

        BigDecimal totalPlatformCommission,

        BigDecimal totalRestaurantNetEarnings,

        List<SuperAdminRestaurantEarningsResponse>
                restaurants

) {
}