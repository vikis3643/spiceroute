package com.aditya.restaurant_backend.dto;

public record SuperAdminCustomerReportResponse(

        long totalCustomers,
        long activeCustomers,
        long inactiveCustomers,

        long verifiedCustomers,
        long unverifiedCustomers,

        long newCustomers,
        long activeNewCustomers

) {
}