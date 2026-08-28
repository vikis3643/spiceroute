package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;

public record DiscountCalculationResult(

        BigDecimal discountAmount,

        String appliedDiscountNames

) {
}