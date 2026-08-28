package com.aditya.restaurant_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.aditya.restaurant_backend.entity.MealSlot;
import com.aditya.restaurant_backend.entity.OrderStatus;
import com.aditya.restaurant_backend.entity.OrderTiming;
import com.aditya.restaurant_backend.entity.PaymentMethod;
import com.aditya.restaurant_backend.entity.PaymentStatus;

public record SuperAdminOrderResponse(

        Long id,

        Long restaurantId,
        String restaurantName,

        Long customerId,
        String customerName,
        String customerEmail,
        String phone,

        String deliveryAddress,
        Double deliveryLatitude,
        Double deliveryLongitude,

        OrderStatus status,

        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String transactionId,

        OrderTiming orderTiming,
        MealSlot mealSlot,
        LocalDateTime scheduledFor,
        LocalDateTime preparationStartAt,

        BigDecimal subtotal,
        BigDecimal discountAmount,
        String appliedDiscountNames,
        BigDecimal deliveryFee,
        BigDecimal totalAmount,

        LocalDateTime createdAt,

        List<SuperAdminOrderItemResponse> items

) {
}