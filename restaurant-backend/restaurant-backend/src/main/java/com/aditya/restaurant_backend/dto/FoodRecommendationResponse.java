package com.aditya.restaurant_backend.dto;

import java.util.List;

import com.aditya.restaurant_backend.entity.MenuItem;

public record FoodRecommendationResponse(

        MenuItem menuItem,

        int matchScore,

        List<String> reasons

) {
}