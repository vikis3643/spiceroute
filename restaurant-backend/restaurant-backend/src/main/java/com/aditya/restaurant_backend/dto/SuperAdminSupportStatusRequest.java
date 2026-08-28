package com.aditya.restaurant_backend.dto;

import com.aditya.restaurant_backend.entity.SupportTicketStatus;

import jakarta.validation.constraints.NotNull;

public record SuperAdminSupportStatusRequest(

        @NotNull(
                message = "Ticket status is required"
        )
        SupportTicketStatus status

) {
}