package com.aditya.restaurant_backend.dto;

import com.aditya.restaurant_backend.entity.RestaurantApprovalStatus;

import jakarta.validation.constraints.NotNull;

public record SuperAdminRestaurantStatusRequest(

        @NotNull(
                message = "Approval status is required"
        )
        RestaurantApprovalStatus approvalStatus

) {
}