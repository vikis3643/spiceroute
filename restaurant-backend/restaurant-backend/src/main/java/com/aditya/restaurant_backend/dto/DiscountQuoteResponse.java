package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;

public record DiscountQuoteResponse(

        BigDecimal subtotal,

        BigDecimal discountAmount,

        String appliedDiscountNames,

        BigDecimal deliveryFee,

        BigDecimal totalAmount

) {
}