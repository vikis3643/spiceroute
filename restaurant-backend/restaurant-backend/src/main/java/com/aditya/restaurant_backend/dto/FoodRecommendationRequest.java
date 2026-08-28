package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;

import com.aditya.restaurant_backend.entity.ProteinLevel;
import com.aditya.restaurant_backend.entity.SpiceLevel;
import com.aditya.restaurant_backend.entity.TasteType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record FoodRecommendationRequest(

        @NotNull(message = "Budget is required")
        @DecimalMin(
                value = "1.00",
                message = "Budget must be at least ₹1"
        )
        BigDecimal maximumBudget,

        Boolean vegetarian,

        SpiceLevel spiceLevel,

        TasteType tasteType,

        ProteinLevel proteinLevel

) {
}