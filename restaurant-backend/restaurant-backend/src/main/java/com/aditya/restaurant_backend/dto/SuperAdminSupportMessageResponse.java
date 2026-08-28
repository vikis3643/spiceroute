package com.aditya.restaurant_backend.dto;

import java.time.LocalDateTime;

public record SuperAdminSupportMessageResponse(

        Long id,
        String senderType,
        String senderName,
        String message,
        LocalDateTime createdAt

) {
}