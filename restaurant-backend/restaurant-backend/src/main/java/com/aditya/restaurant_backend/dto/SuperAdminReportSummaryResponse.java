package com.aditya.restaurant_backend.dto;

import java.time.LocalDate;
import java.util.List;

public record SuperAdminReportSummaryResponse(

        LocalDate startDate,
        LocalDate endDate,

        SuperAdminCustomerReportResponse customers,

        SuperAdminOrderReportResponse orders,

        List<SuperAdminRestaurantPerformanceResponse>
                restaurants

) {
}