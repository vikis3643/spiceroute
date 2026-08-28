package com.aditya.restaurant_backend.dto;

import java.time.LocalDateTime;

import com.aditya.restaurant_backend.entity.CustomerReview;

public record ReviewResponse(
        Long id,
        Long orderId,
        String customerName,
        int foodRating,
        int customerServiceRating,
        String comment,
        LocalDateTime createdAt
) {

    public static ReviewResponse from(
            CustomerReview review
    ) {
        return new ReviewResponse(
                review.getId(),
                review.getOrder().getId(),
                review.getCustomerAccount()
                        .getFullName(),
                review.getFoodRating(),
                review.getCustomerServiceRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}