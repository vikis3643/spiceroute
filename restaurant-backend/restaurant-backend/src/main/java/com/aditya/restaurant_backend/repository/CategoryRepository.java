package com.aditya.restaurant_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aditya.restaurant_backend.entity.Category;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    // ==========================================
    // EXISTING METHODS
    // Temporary compatibility with old system
    // ==========================================

    Optional<Category> findByNameIgnoreCase(
            String name
    );

    boolean existsByNameIgnoreCase(
            String name
    );

    // ==========================================
    // MULTI-RESTAURANT METHODS
    // ==========================================

    List<Category>
            findByRestaurantIdOrderByNameAsc(
                    Long restaurantId
            );

    List<Category>
            findByRestaurantIdAndActiveTrueOrderByNameAsc(
                    Long restaurantId
            );

    Optional<Category>
            findByIdAndRestaurantId(
                    Long categoryId,
                    Long restaurantId
            );

    Optional<Category>
            findByRestaurantIdAndNameIgnoreCase(
                    Long restaurantId,
                    String name
            );

    boolean existsByRestaurantIdAndNameIgnoreCase(
            Long restaurantId,
            String name
    );
}