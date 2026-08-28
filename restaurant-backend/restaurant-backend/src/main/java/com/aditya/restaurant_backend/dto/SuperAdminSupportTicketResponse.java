package com.aditya.restaurant_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.aditya.restaurant_backend.entity.SupportTicketCategory;
import com.aditya.restaurant_backend.entity.SupportTicketPriority;
import com.aditya.restaurant_backend.entity.SupportTicketStatus;

public record SuperAdminSupportTicketResponse(

        Long id,

        Long restaurantId,
        String restaurantName,

        Long customerId,
        String customerName,
        String customerEmail,

        String subject,
        SupportTicketCategory category,
        SupportTicketPriority priority,
        SupportTicketStatus status,

        Long orderId,

        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        List<SuperAdminSupportMessageResponse> messages

) {
}