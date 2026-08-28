package com.aditya.restaurant_backend.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record DiscountQuoteRequest(

        @Valid
        @NotEmpty(
                message = "Cart must contain at least one item"
        )
        List<OrderItemRequest> items

) {
}