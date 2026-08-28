package com.aditya.restaurant_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.Discount;

public interface DiscountRepository
        extends JpaRepository<Discount, Long> {

    // ==========================================
    // EXISTING METHOD
    // Existing discount calculation compatibility
    // ==========================================

    List<Discount> findByActiveTrue();

    // ==========================================
    // MULTI-RESTAURANT METHODS
    // ==========================================

    List<Discount>
            findByRestaurantIdOrderByCreatedAtDesc(
                    Long restaurantId
            );

    List<Discount>
            findByRestaurantIdAndActiveTrueOrderByCreatedAtDesc(
                    Long restaurantId
            );

    Optional<Discount>
            findByIdAndRestaurantId(
                    Long discountId,
                    Long restaurantId
            );

    long countByRestaurantId(
            Long restaurantId
    );
}