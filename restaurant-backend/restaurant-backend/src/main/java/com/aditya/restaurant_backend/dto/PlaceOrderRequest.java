package com.aditya.restaurant_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.aditya.restaurant_backend.entity.MealSlot;
import com.aditya.restaurant_backend.entity.OrderTiming;
import com.aditya.restaurant_backend.entity.PaymentMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PlaceOrderRequest(

        @NotBlank(message = "Customer name is required")
        @Size(
                max = 100,
                message = "Customer name cannot exceed 100 characters"
        )
        String customerName,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Phone number must contain exactly 10 digits"
        )
        String phone,

        @NotBlank(message = "Delivery address is required")
        @Size(
                min = 10,
                max = 1000,
                message = "Delivery address must contain 10 to 1000 characters"
        )
        String deliveryAddress,

        @DecimalMin(
                value = "-90.0",
                message = "Delivery latitude cannot be below -90"
        )
        @DecimalMax(
                value = "90.0",
                message = "Delivery latitude cannot exceed 90"
        )
        Double deliveryLatitude,

        @DecimalMin(
                value = "-180.0",
                message = "Delivery longitude cannot be below -180"
        )
        @DecimalMax(
                value = "180.0",
                message = "Delivery longitude cannot exceed 180"
        )
        Double deliveryLongitude,

        PaymentMethod paymentMethod,

        OrderTiming orderTiming,

        MealSlot mealSlot,

        @Future(
                message = "Scheduled order time must be in the future"
        )
        LocalDateTime scheduledFor,

        @Valid
        @NotEmpty(message = "Order must contain at least one item")
        List<OrderItemRequest> items

) {
}