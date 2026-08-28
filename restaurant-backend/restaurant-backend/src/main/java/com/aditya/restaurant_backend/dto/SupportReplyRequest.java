package com.aditya.restaurant_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupportReplyRequest(

        @NotBlank(message = "Reply message is required")
        @Size(
                min = 2,
                max = 2000,
                message = "Reply must contain 2 to 2000 characters"
        )
        String message

) {
}