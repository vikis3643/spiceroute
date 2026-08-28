package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;

public record SuperAdminPaymentSummaryResponse(

        long totalOrders,

        long cashOnDeliveryOrders,
        long razorpayOrders,

        long paidOrders,
        long pendingPayments,
        long failedPayments,

        BigDecimal totalPaidAmount

) {
}