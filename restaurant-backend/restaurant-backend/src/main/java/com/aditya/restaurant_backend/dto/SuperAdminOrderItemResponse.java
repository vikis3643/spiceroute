package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;

public record SuperAdminOrderItemResponse(

        Long id,
        Long menuItemId,
        String itemName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal

) {
}