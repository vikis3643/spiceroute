package com.aditya.restaurant_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.CustomerReview;

public interface CustomerReviewRepository
        extends JpaRepository<CustomerReview, Long> {

    // ==========================================
    // EXISTING METHODS
    // ==========================================

    Optional<CustomerReview>
            findByOrderId(
                    Long orderId
            );

    boolean existsByOrderId(
            Long orderId
    );

    List<CustomerReview>
            findAllByOrderByCreatedAtDesc();

    // ==========================================
    // MULTI-RESTAURANT REVIEW METHODS
    // ==========================================

    List<CustomerReview>
            findByOrder_Restaurant_IdOrderByCreatedAtDesc(
                    Long restaurantId
            );

    Optional<CustomerReview>
            findByIdAndOrder_Restaurant_Id(
                    Long reviewId,
                    Long restaurantId
            );

    Optional<CustomerReview>
            findByOrderIdAndOrder_Restaurant_Id(
                    Long orderId,
                    Long restaurantId
            );

    long countByOrder_Restaurant_Id(
            Long restaurantId
    );
}