package com.aditya.restaurant_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ReviewRequest(

        @Min(
                value = 1,
                message = "Food rating must be at least 1"
        )
        @Max(
                value = 5,
                message = "Food rating cannot exceed 5"
        )
        int foodRating,

        @Min(
                value = 1,
                message = "Customer service rating must be at least 1"
        )
        @Max(
                value = 5,
                message = "Customer service rating cannot exceed 5"
        )
        int customerServiceRating,

        @Size(
                max = 1000,
                message = "Review comment cannot exceed 1000 characters"
        )
        String comment
) {
}