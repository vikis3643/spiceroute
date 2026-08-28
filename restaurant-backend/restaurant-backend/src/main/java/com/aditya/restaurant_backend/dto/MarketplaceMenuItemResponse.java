package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;

public record MarketplaceMenuItemResponse(

        Long id,

        String name,
        String description,
        BigDecimal price,
        String imageUrl,

        boolean vegetarian,
        boolean available,

        String spiceLevel,
        String tasteType,
        String proteinLevel,

        Long categoryId,
        String categoryName,

        Long restaurantId,
        String restaurantName,
        String restaurantCity,
        String restaurantState

) {
}