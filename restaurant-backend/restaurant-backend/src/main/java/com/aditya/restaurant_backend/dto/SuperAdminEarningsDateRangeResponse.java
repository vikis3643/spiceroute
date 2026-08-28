package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SuperAdminEarningsDateRangeResponse(

        LocalDate startDate,
        LocalDate endDate,

        long totalDeliveredOrders,

        BigDecimal totalDeliveredSubtotal,

        BigDecimal totalPlatformCommission,

        BigDecimal totalRestaurantNetEarnings,

        List<SuperAdminRestaurantEarningsResponse>
                restaurants

) {
}