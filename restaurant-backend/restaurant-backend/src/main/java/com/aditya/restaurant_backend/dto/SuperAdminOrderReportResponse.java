package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;

public record SuperAdminOrderReportResponse(

        long totalOrders,
        long placedOrders,
        long confirmedOrders,
        long preparingOrders,
        long readyOrders,
        long outForDeliveryOrders,
        long deliveredOrders,
        long cancelledOrders,

        BigDecimal deliveredRevenue

) {
}