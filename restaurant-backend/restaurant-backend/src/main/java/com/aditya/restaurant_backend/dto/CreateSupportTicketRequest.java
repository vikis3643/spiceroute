package com.aditya.restaurant_backend.dto;

import com.aditya.restaurant_backend.entity.SupportTicketCategory;
import com.aditya.restaurant_backend.entity.SupportTicketPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSupportTicketRequest(

        @NotBlank(message = "Subject is required")
        @Size(
                min = 5,
                max = 150,
                message = "Subject must contain 5 to 150 characters"
        )
        String subject,

        @NotNull(message = "Category is required")
        SupportTicketCategory category,

        @NotNull(message = "Priority is required")
        SupportTicketPriority priority,

        @NotNull(message = "Restaurant is required")
        Long restaurantId,

        Long orderId,

        @NotBlank(message = "Message is required")
        @Size(
                min = 10,
                max = 2000,
                message = "Message must contain 10 to 2000 characters"
        )
        String message

) {
}