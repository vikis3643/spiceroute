package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;

public record SuperAdminDashboardSummary(

        long totalRestaurants,
        long pendingRestaurants,
        long approvedRestaurants,
        long activeRestaurants,

        long totalRestaurantAdmins,
        long totalCustomers,

        long totalOrders,
        long deliveredOrders,
        long cancelledOrders,

        BigDecimal deliveredRevenue

) {
}